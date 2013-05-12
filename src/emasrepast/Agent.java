package emasrepast;

import java.util.ArrayList;
import java.util.List;

import communication.config.ConfigReader;

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

public class Agent {

	private static final double REPRODUCING_THRESHOLD = 0.6;
	private static final double DYING_THRESHOLD = 0.3;
	private static final int MAX_FIT = 20;
	private static int currentId = 0;
	private String id;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;
	private Island island;
	private int lastStep = 0; //debug only

	public Agent(Island currentIsland, int energy) {
		this.id = currentId++ + "#" + currentIsland.getId() + "@" + ConfigReader.getLocalHost();
		this.island = currentIsland;
		this.energy = energy;
		this.startingEnergy = energy;
		this.space = currentIsland.getSpace();
		this.grid = currentIsland.getGrid();
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		lastStep++;
		System.out.println(lastStep + " small step(s) for an agent: " + id);
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

		moveTowards(randomPoint);
		meetOtherAgents();

//		if (this.isLikelyToDie()) {
		tryToDie();
//		}

	}

	public String getId() {
		return id;
	}

	public boolean isAbleToReproduce() {
		return energy > REPRODUCING_THRESHOLD * startingEnergy;
	}

	public boolean isLikelyToDie() {
		return energy < DYING_THRESHOLD * startingEnergy;
	}

	public void tryToDie() {
		double ratio = energy / (startingEnergy + 0.0);
		double rand = RandomHelper.nextDoubleFromTo(0, DYING_THRESHOLD);

		if (rand > ratio) {
			// dying
			ContextUtils.getContext(this).remove(this);
		}
	}

	public void tryToReproduceWith(Agent other) {

//		Context<Object> context = ContextUtils.getContext(this);

		int mine = this.computeFitness();
		int his = other.computeFitness();

		if (mine + his > MAX_FIT) {

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

	public void exchangeEnergiesWith(Agent other) {
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

	public void moveTowards(GridPoint pt) {
		// only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint,
					otherPoint);
			space.moveByVector(this, 2, angle, 0);
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
//			energy -= 1;
		}
	}

	public void meetOtherAgents() {
		GridPoint pt = grid.getLocation(this);
		List<Object> others = new ArrayList<Object>();
		for (Object obj : grid.getObjectsAt(pt.getX(), pt.getY())) {
			if (obj instanceof Agent && !obj.equals(this)) {
				others.add(obj);
			}
		}
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

	public int computeFitness() {
		// or other behaviour
		return this.rollTheDice();
	}

	public int rollTheDice() {
		return RandomHelper.nextIntFromTo(0, MAX_FIT);
	}

	public void increaseEnergy(int n) {
		this.energy += n;
	}

	public void decreaseEnergy(int n) {
		this.energy -= n;
	}

	public int getEnergy() {
		return this.energy;
	}
	
	public int getStartingEnergy() {
		return startingEnergy;
	}
}
