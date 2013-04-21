package emasrepast;

import repast.simphony.context.DefaultContext;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Island extends DefaultContext<Object>{
	
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public Island(String id) {
		super(id);
	}

	public ContinuousSpace<Object> getSpace() {
		return space;
	}

	public void setSpace(ContinuousSpace<Object> space) {
		this.space = space;
	}

	public Grid<Object> getGrid() {
		return grid;
	}

	public void setGrid(Grid<Object> grid) {
		this.grid = grid;
	}
	
	public int getAgentCount(){
		return this.size();
	}
	
}
