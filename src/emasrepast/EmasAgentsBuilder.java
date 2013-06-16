package emasrepast;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

import communication.Server;
import communication.ServerHolder;

public class EmasAgentsBuilder implements ContextBuilder<Object> {

	private static final String MAIN_CONTEXT = "emasrepast"; 
	
	private static Context<Object> mainContext;
	public static ArrayList<Island> islands;
	public static int ISLANDS_COUNT;
	private static AtomicInteger travellingCount = new AtomicInteger(0);

	public static void incrementTravellingCount() {
		travellingCount.addAndGet(1);
	}
	
	public static int getTravellingCount() {
		return travellingCount.get();
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public Context build(Context<Object> context) {

		Parameters params = RunEnvironment.getInstance().getParameters();
		ISLANDS_COUNT = (Integer) params.getValue("islandsPerNode");
		
		mainContext = context;
		mainContext.setId(MAIN_CONTEXT);

		islands = new ArrayList<Island>();
		
		for(int i = 0; i< ISLANDS_COUNT; i++){	
			
			Island subcontext = new Island("island"+i);
			
			ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder
					.createContinuousSpaceFactory(null);
			ContinuousSpace<Object> space = spaceFactory.createContinuousSpace(
					"space" + i, subcontext, new RandomCartesianAdder<Object>(),
					new repast.simphony.space.continuous.WrapAroundBorders(), 50,
					50);
	
			GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
			Grid<Object> grid = gridFactory.createGrid("grid" + i, subcontext,
					new GridBuilderParameters<Object>(new WrapAroundBorders(),
							new SimpleGridAdder<Object>(), true, 50, 50));
			
			subcontext.setSpace(space);
			subcontext.setGrid(grid);
			
			mainContext.addSubContext(subcontext);
			islands.add(subcontext);
		}
		
		
		
		try {
			ServerHolder.setServer(new Server(mainContext, islands));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		int currentIslandIndex = 0;
		Island currentIsland;
		
		int agentCount = (Integer)params.getValue("agentCount"); //100;
		for (int i = 0; i < agentCount; i++) {
			
			currentIsland = islands.get(currentIslandIndex);
			int energy = RandomHelper.nextIntFromTo(30, 50);
			
			currentIsland.add(new Agent(currentIsland, energy));
			
			currentIslandIndex++;
			if (currentIslandIndex % ISLANDS_COUNT == 0){
				currentIslandIndex = 0;
			}
		}
		
		for (Island subcontext : islands){
			System.out.println(subcontext.getId() + ": " + subcontext.getAgentCount() + " agents");
			for (Object obj : subcontext) {
				NdPoint pt = subcontext.getSpace().getLocation(obj);
				subcontext.getGrid().moveTo(obj, (int) pt.getX(), (int) pt.getY());
			}
		}

		return mainContext;
	}

}
