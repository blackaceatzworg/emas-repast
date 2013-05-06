package runner;

public class ManualRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ManualRun mr = new ManualRun();
		mr.start();

	}

	public void start() {
		String[] args = new String[] { "D:\\dev\\miss\\repastws\\emasrepast\\emasrepast.rs\\",
				"C:\\dev\\RepastSimphony-2.0\\eclipse\\plugins\\repast.simphony.runtime_2.0.1\\"};
		
		repast.simphony.runtime.RepastMain.main(args);

	}

}
