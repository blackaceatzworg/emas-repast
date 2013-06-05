package communication.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import communication.Node;
import communication.config.ConfigReader;

public class RegistryContainer extends UnicastRemoteObject implements NodesContainer {
	private static final long serialVersionUID = 1L;
	private List <Node> nodes = new LinkedList <Node>();
	private Map<String, Long> times = new HashMap<String, Long>();
	
	public RegistryContainer () throws RemoteException{
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
		for (Node node : nodes){
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
		Long start = times.get(name);
		if (start != null){
			System.err.println("Execution time of node " + name + ": " + (System.nanoTime() - start)/1000000000.0 + "s");
			times.remove(name);
		}
		
		nodes.remove(node);
	}
}
