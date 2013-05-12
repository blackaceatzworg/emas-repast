package runner;

import java.io.File;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;

public class ManualRunnerMain {

	public static void main(String[] args){

		args[0] = (System.getProperty("user.dir")) + "\\emasrepast.rs\\";
		File file = new File(args[0]); // the scenario dir

		ManualRunner runner = new ManualRunner();
		ParametersCreator creator = new ParametersCreator();
		//Parameters params2 = creator.createParameters();
		creator.addParameter("agent_count", Integer.class, 20, false);
		creator.addParameter("randomSeed", Integer.class, 1489240545, false);
		creator.addConvertor("agent_count", new repast.simphony.parameter.StringConverterFactory.IntConverter());
		creator.addConvertor("randomSeed", new repast.simphony.parameter.StringConverterFactory.IntConverter());
		Parameters params = creator.createParameters();
		
		try {
			runner.load(file, params);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

		double endTime = 10.0;  // some arbitrary end time

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
	}
}