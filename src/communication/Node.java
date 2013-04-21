package communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
    String addAgent(Integer energy) throws RemoteException;
}