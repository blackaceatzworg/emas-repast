package emasrepast;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Agent {

	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	private int energy, startingEnergy;

	public Agent(ContinuousSpace<Object> space, Grid<Object> grid, int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = startingEnergy = energy;
	}

	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
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
//		int maxCount = -1;
//		for (GridCell<Agent> cell : gridCells) {
//			if (cell.size() > maxCount) {
//				pointWithMostAgents = cell.getPoint();
//				maxCount = cell.size();
//			}
//		}
		
		
		if (energy > 0) {
			moveTowards(randomPoint);
			greet();
			
		} else {
			// stopped moving
			energy = 0;// startingEnergy;
			
			//// dying
			//Context<Object> context = ContextUtils.getContext(this);
			//context.remove(this);
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
			energy--;
		}
	}

	public void greet() {
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
			
//			NdPoint spacePt = space.getLocation(obj);			
//			dying
			Context<Object> context = ContextUtils.getContext(obj);
//			context.remove(obj);
//			
//			creating new agent
//			int energy = 10;
//			Agent agent = new Agent(space, grid, energy);
//			context.add(agent);
//			space.moveTo(agent, spacePt.getX(), spacePt.getY());
//			grid.moveTo(agent, pt.getX(), pt.getY());

			Network<Object> net = (Network<Object>) context.getProjection("greetings network");
			
			Agent other = (Agent) obj;
			int mine = this.rollTheDice();
			int his = other.rollTheDice();
			if (mine > his){
				this.increaseEnergy(5);
				other.decreaseEnergy(5);
								
				net.addEdge(this, obj);
			} else if(mine < his) {
				this.decreaseEnergy(5);
				other.increaseEnergy(5);
				
				net.addEdge(obj, this);
			} else {
				net.addEdge(this, obj);
				net.addEdge(obj, this);
			}
									
		}
	}
	
	public int rollTheDice(){
		return RandomHelper.nextIntFromTo(0, 20);
	}

	public void increaseEnergy(int n){
		this.energy += n;
	}
	
	public void decreaseEnergy(int n){
		this.energy -= n;
	}
	
	public int getEnergy(){
		return this.energy;
	}
}
