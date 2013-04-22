package communication.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import communication.Node;


public interface NodesContainer extends Remote{
	void addNode (Node node) throws RemoteException;
	Node getNodeByName (String name) throws RemoteException;
}
