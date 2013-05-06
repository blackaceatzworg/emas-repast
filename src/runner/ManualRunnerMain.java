package runner;

import java.io.File;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;
import repast.simphony.visualization.visualization2D.RepastCanvas2D.repastPiccoloMouseMotionListener;

public class ManualRunnerMain {

	public static void main(String[] args){

		args[0] = "D:\\dev\\miss\\repastws\\emasrepast\\emasrepast.rs\\";
//		args[1] = "C:\\dev\\RepastSimphony-2.0\\eclipse\\plugins\\repast.simphony.runtime_2.0.1\\";		
		File file = new File(args[0]); // the scenario dir

		ManualRunner runner = new ManualRunner();
		ParametersCreator creator = new ParametersCreator();
		Parameters params2 = creator.createParameters();
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

		double endTime = 1000.0;  // some arbitrary end time

		// Run the sim a few times to check for cleanup and init issues.
		for(int i=0; i<2; i++){

			runner.runInitialize(params);  // initialize the run

			RunEnvironment.getInstance().endAt(endTime);

			while (runner.getActionCount() > 0){  // loop until last action is left
				if (runner.getModelActionCount() == 0) {
					runner.setFinishing(true);
				}
				runner.step();  // execute all scheduled actions at next tick

			}

			runner.stop();          // execute any actions scheduled at run end
			runner.cleanUpRun();
		}
		runner.cleanUpBatch();    // run after all runs complete
	}
}