package emasrepast;

import repast.simphony.context.DefaultContext;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;

public class Island extends DefaultContext<Object>{
	
//	private final String id;
	private ContinuousSpace<Object> space;
	private Grid<Object> grid;
	
	public Island(String id) {
		super(id);
//		this.id = id;
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

//	public String getId() {
//		return id; 
//	}
	
	public int getAgentCount(){
		return this.size();
	}
	
}
