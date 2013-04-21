package emasrepast;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
//import repast.simphony.context.space.graph.NetworkBuilder;
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

public class EmasAgentsBuilder implements ContextBuilder<Object> {

	private static final String MAIN_CONTEXT = "emasrepast"; //"maincontext"; 
	
	private static Context<Object> mainContext;
	public static ArrayList<Island> islands;
	public static final int islandsCount = 2;
	
	
	@Override
	public Context build(Context<Object> context) {

		mainContext = context;
		mainContext.setId(MAIN_CONTEXT);

		islands = new ArrayList<Island>();
		
		for(int i = 0; i< islandsCount; i++){	
//			NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>(
//					"greetings network", context, true);
//			netBuilder.buildNetwork();
			
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
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		
		// TODO integrate with servers
//		new Server(context, space, grid);
		
		int currentIslandIndex = 0;
		Island currentIsland;
		
		int agentCount = (Integer)params.getValue("agent_count"); //100;
		for (int i = 0; i < agentCount; i++) {
			
			currentIsland = islands.get(currentIslandIndex);
			int energy = RandomHelper.nextIntFromTo(30, 50);
			
			// TODO agent must know his current island, not current space and grid
			currentIsland.add(new Agent(currentIsland.getSpace(), currentIsland.getGrid(), energy));
			
			currentIslandIndex++;
			if (currentIslandIndex % islandsCount == 0){
				currentIslandIndex = 0;
			}
		}
		int ind = 0;
		for (Island subcontext : islands){
			ind++;
			System.out.println("island"+ind+ ": " + subcontext.getAgentCount() + " agents");
			for (Object obj : subcontext) {
				NdPoint pt = subcontext.getSpace().getLocation(obj);
				subcontext.getGrid().moveTo(obj, (int) pt.getX(), (int) pt.getY());
			}
		}

		return mainContext;
	}

}
