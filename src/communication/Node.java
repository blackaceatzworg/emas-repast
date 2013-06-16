package communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
    String addAgent(Integer energy, Integer startingEnergy) throws RemoteException;
    int getAgentCount() throws RemoteException;
    String getName() throws RemoteException;
	void cleanUp() throws RemoteException;
	int getTravelCount() throws RemoteException;
}