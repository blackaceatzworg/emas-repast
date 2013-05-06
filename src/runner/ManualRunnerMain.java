package runner;

import java.io.File;

public class ManualRunnerMain {

	public static void main(String[] args){

		args[0] = "D:\\dev\\miss\\repastws\\emasrepast\\emasrepast.rs\\";
//		args[1] = "C:\\dev\\RepastSimphony-2.0\\eclipse\\plugins\\repast.simphony.runtime_2.0.1\\";		
		File file = new File(args[0]); // the scenario dir

		ManualRunner runner = new ManualRunner();

		try {
			runner.load(file);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}

//		double endTime = 1000.0;  // some arbitrary end time

		// Run the sim a few times to check for cleanup and init issues.
		for(int i=0; i<2; i++){

			runner.runInitialize();  // initialize the run

//			RunEnvironment.getInstance().endAt(endTime);

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