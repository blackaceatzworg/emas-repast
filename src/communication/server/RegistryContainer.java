package communication.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

import communication.Node;
import communication.config.ConfigReader;

public class RegistryContainer extends UnicastRemoteObject implements NodesContainer {
	private static final long serialVersionUID = 1L;
	private List <Node> nodes = new LinkedList <Node>();
	
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
}
