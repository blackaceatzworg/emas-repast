package communication.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import communication.Node;
import communication.config.ConfigReader;

public class RegistryContainer extends UnicastRemoteObject implements
		NodesContainer {
	private static final long serialVersionUID = 1L;
	private List<Node> nodes = new LinkedList<Node>();
	private Map<String, Long> times = new HashMap<String, Long>();
	private Map<String, Double> elapsed = new HashMap<String, Double>();
	private int travelSum = 0;
//	private int runCount = 0;

	public RegistryContainer() throws RemoteException {
		Integer rmiPort = ConfigReader.getRmiPort();

		try {
			Registry registry = LocateRegistry.createRegistry(rmiPort);
			registry.rebind("RegistryContainer", this);
			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			new RegistryContainer();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void addNode(Node node) {
		nodes.add(node);
		String name = "";
		try {
			name = node.getName();
			times.put(name, System.nanoTime());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Node getNodeByName(String name) {
		for (Node node : nodes) {
			try {
				if (node.getName().equals(name))
					return node;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void unregisterNode(Node node) throws RemoteException {
		String name = node.getName();
		int travelCount = node.getTravelCount();
		Long start = times.get(name);
		if (start != null) {
			Double elapsedTime = (System.nanoTime() - start) / 1000000000.0;
			System.err.println("time of " + name + "="
					+ elapsedTime + "s, "
					+ travelCount + "travels");
			elapsed.put(name, elapsedTime);
			travelSum += travelCount;
			times.remove(name);
		}

		nodes.remove(node);
		if (times.isEmpty()) {
			double maxElapsed = Collections.max(elapsed.values());
			double avgElapsed = 0;
			for (Double d : elapsed.values()){
				avgElapsed += d;
			}
			avgElapsed /= elapsed.size();
			
			System.err.println("MAX elapsed time:" + maxElapsed);
			System.err.println("AVG elapsed time:" + avgElapsed);
			System.err.println("Travel Count Sum:" + travelSum);
			System.err.println("Travels/MAX:" + (travelSum/maxElapsed));
//			runCount++;
//			System.err.println(runCount + "runs passed ########################");
			System.err.println("#");
			travelSum = 0;
			elapsed.clear();
		}
	}
}
