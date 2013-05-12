package runner;

import java.io.File;
import java.rmi.RemoteException;

import communication.ServerHolder;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;

public class ManualRunnerMain {

	private static Parameters loadParameters(int agentCount, int islandsPerNode){
		ParametersCreator creator = new ParametersCreator();
		creator.addParameter("agentCount", Integer.class, agentCount, false);
		creator.addParameter("islandsPerNode", Integer.class, islandsPerNode, false);
		
		creator.addParameter("randomSeed", Integer.class, 1489240545, false);
		creator.addConvertor("agentCount", new repast.simphony.parameter.StringConverterFactory.IntConverter());
		creator.addConvertor("islandsPerNode", new repast.simphony.parameter.StringConverterFactory.IntConverter());
		creator.addConvertor("randomSeed", new repast.simphony.parameter.StringConverterFactory.IntConverter());
		Parameters params = creator.createParameters();
		return params;
	}
	
	private static Parameters loadParameters() {
		return loadParameters(30, 3);
	}
	
	public static void main(String[] args){

		args[0] = (System.getProperty("user.dir")) + "\\emasrepast.rs\\";
		File file = new File(args[0]); // the scenario dir

		long startTime = System.nanoTime();
				
		int agentCount = 100;
		int islandsPerNode = 2;
		
		
		ManualRunner runner = new ManualRunner();
		Parameters params = loadParameters(agentCount, islandsPerNode);
				
		try {
			runner.load(file, params);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		double endTime = 100.0;  // some arbitrary end time

		runner.runInitialize(params);  // initialize the run

		RunEnvironment.getInstance().endAt(endTime);

		while (runner.getModelActionCount() != 1) {
			// System.out.println(runner.getActionCount() + " " + runner.getModelActionCount());
			runner.step();  // execute all scheduled actions at next tick
		}
		runner.setFinishing(true);
		runner.stop();          // execute any actions scheduled at run end
		runner.cleanUpRun();
		runner.cleanUpBatch();    // run after all runs complete
		try {
			ServerHolder.getServer().cleanUp();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		System.out.println("Final execution time of " + agentCount + " agents on " + islandsPerNode + " islands:");
		System.out.println((System.nanoTime()-startTime)/1000000000.0+"s");
		
		
	}
}