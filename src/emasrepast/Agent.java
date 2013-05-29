package emasrepast;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

import communication.Node;
import communication.config.ConfigReader;
import communication.server.NodesContainer;

public class Agent {

	private static final int WORK_STEPS = 500000;
	private static final double REPRODUCING_THRESHOLD = 0.9;
	private static final double TRAVELLING_THRESHOLD = 1.16;
	private static final double DYING_THRESHOLD = 0.3;
	private static final int MAX_FIT = 20;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;
	private Island island;
	private boolean _readyToWork;

	public void setReadyToWork(boolean readyToWork) {
		this._readyToWork = readyToWork;
	}

	public Agent(Island currentIsland, int energy) {
		this.island = currentIsland;
		this.energy = energy;
		this.startingEnergy = energy;
		this.space = currentIsland.getSpace();
		this.grid = currentIsland.getGrid();
		_readyToWork = true; //when agent has created at startup, then it is already ready to work
	}
	
	public Agent(Island currentIsland, int energy, int startingEnergy){
		this(currentIsland, energy);
		this.startingEnergy = startingEnergy;
		_readyToWork = false; //agent has migrated, it is not ready to work yet
	}

	private int doSomeHeavyWork (int n, int a){
		while (n > 3)
		{
			a = a * a % n;
			n--;
		}
		return a;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		if (!readyToWork())
			return;
//		System.out.println("STEP. Island: "+island);
		// get the grid location of this agent
		GridPoint pt = grid.getLocation(this);

		// use the GridCellNgh class to create GridCells for
		// the surrounding neighborhood.
		GridCellNgh<Agent> nghCreator = new GridCellNgh<Agent>(grid, pt,
				Agent.class, 5, 5);
		List<GridCell<Agent>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		int randomIndex = RandomHelper.nextIntFromTo(0, gridCells.size() - 1);
		GridPoint randomPoint = gridCells.get(randomIndex).getPoint();
		// int maxCount = -1;
		// for (GridCell<Agent> cell : gridCells) {
		// if (cell.size() > maxCount) {
		// pointWithMostAgents = cell.getPoint();
		// maxCount = cell.size();
		// }
		// }
		//System.out.println("STARTING_ENERGY: "+startingEnergy + " ENERGY: " + energy);
		
		doSomeHeavyWork(WORK_STEPS, RandomHelper.nextInt());

		moveTowards(randomPoint);
		meetOrTravel();

//		if (this.isLikelyToDie()) {
		tryToDie();
//		}

	}

	private boolean readyToWork() {
		return _readyToWork;
	}

	private boolean isAbleToReproduce() {
		return energy > REPRODUCING_THRESHOLD * startingEnergy;
	}

	/*private boolean isLikelyToDie() {
		return energy < DYING_THRESHOLD * startingEnergy;
	}*/

	private boolean isAbleToTravel() {
		return energy > TRAVELLING_THRESHOLD * startingEnergy;
	}
	
	private void tryToDie() {
		double ratio = energy / (startingEnergy + 0.0);
		double rand = RandomHelper.nextDoubleFromTo(0, DYING_THRESHOLD);

		if (rand > ratio) {
			// dying
			System.out.println("DYING");
			ContextUtils.getContext(this).remove(this);
		}
	}

	private void tryToReproduceWith(Agent other) {

//		Context<Object> context = ContextUtils.getContext(this);

		int mine = this.computeFitness();
		int his = other.computeFitness();

		if (mine + his > MAX_FIT) {
			System.out.println("REPRODUCING");
			// // creating new agent
			GridPoint pt = grid.getLocation(this);
			NdPoint spacePt = space.getLocation(this);
			
			int energy = (this.getStartingEnergy() + other.getStartingEnergy()) / 2;
			Agent agent = new Agent(island, energy);
			island.add(agent);
			//			context.add(agent);
			space.moveTo(agent, spacePt.getX(), spacePt.getY());
			grid.moveTo(agent, pt.getX(), pt.getY());

			this.decreaseEnergy(energy/2);
			other.decreaseEnergy(energy/2);
		}
	}

	private void exchangeEnergiesWith(Agent other) {
//		Context<Object> context = ContextUtils.getContext(other);

//		Network<Object> net = (Network<Object>) context
//				.getProjection("greetings network");

		// Agent other = (Agent) obj;
		int mine = this.computeFitness();
		int his = other.computeFitness();
		int diff = MAX_FIT / 5;
		if (mine > his) {
			this.increaseEnergy(diff);
			other.decreaseEnergy(diff);

//			net.addEdge(this, other);
		} else if (mine < his) {
			this.decreaseEnergy(diff);
			other.increaseEnergy(diff);

//			net.addEdge(other, this);
		} else {
//			net.addEdge(this, other);
//			net.addEdge(other, this);
		}
	}

	private void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
		}
	}

	private void meetOrTravel() {
		GridPoint pt = grid.getLocation(this);
		List<Object> others = new ArrayList<Object>();
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Agent && !obj.equals(this)) {
				others.add(obj);
			}
		}
		if (this.isAbleToTravel() && !this.isAbleToReproduce()){
			travel();
		}
		else if (!this.isAbleToTravel() && this.isAbleToReproduce()){
			tryToReproduce(others);
		}
		else if (this.isAbleToTravel() && this.isAbleToReproduce()){
			if (Math.random()<0.5)
				travel();
			else
				tryToReproduce(others);
		}		
	}
	
	private void travel() {
		System.out.println("TRAVELLING");
		String rmiHost = ConfigReader.getRmiHost();
        Integer rmiPort = ConfigReader.getRmiPort();
        List <String> hosts = ConfigReader.getHosts();
        String targetHost = hosts.get(RandomHelper.nextIntFromTo(0, hosts.size()-1));
        
        try {
			Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
			NodesContainer stub = (NodesContainer) registry.lookup("RegistryContainer");
			
			Node node = stub.getNodeByName("NodeServer"+targetHost);
			node.addAgent(energy, startingEnergy);
//			System.out.println("TRAVELLING");
//			for (Island isl : EmasAgentsBuilder.islands){
//				System.out.print(isl.getId() + ": " + isl.getAgentCount()+"\t| ");
//			}
//			System.out.println();
			ContextUtils.getContext(this).remove(this);
        } catch (RemoteException e) {
            System.err.println("Target host: " + targetHost + " not ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void tryToReproduce(List <Object> others){
		if (others.size() > 0) {
			int index = RandomHelper.nextIntFromTo(0, others.size() - 1);
			Object obj = others.get(index);

			Agent other = (Agent) obj;
			if (this.isAbleToReproduce() && other.isAbleToReproduce()) {
				this.tryToReproduceWith(other);
			} else {
				this.exchangeEnergiesWith(other);
			}
		}
	}

	

	private int computeFitness() {
		// or other behaviour
		return this.rollTheDice();
	}

	private int rollTheDice() {
		return RandomHelper.nextIntFromTo(0, MAX_FIT);
	}

	private void increaseEnergy(int n) {
		this.energy += n;
	}

	private void decreaseEnergy(int n) {
		this.energy -= n;
	}

	public int getEnergy() {
		return this.energy;
	}
	
	public int getStartingEnergy() {
		return startingEnergy;
	}
}
