package communication;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;

import communication.config.ConfigReader;

import emasrepast.Agent;
import emasrepast.Island;

public class Server implements Node {

	private Context<Object> context;
	private List<Island> islands;


	public Server(Context<Object> mainContext, List<Island> islands) {
		try {
			this.context = mainContext;
			this.islands = islands;
			Node stub = (Node) UnicastRemoteObject.exportObject(this, 0);
			String rmiHost = ConfigReader.getRmiHost();
			Integer rmiPort = ConfigReader.getRmiPort();
			Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
			registry.bind("NodeServer"+ConfigReader.getLocalHost(), stub);
			System.err.println("Server ready");
		} catch (Exception e){
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}				
	}

	public String addAgent(Integer energy) throws RemoteException{
		int islandCount = islands.size();
		int targetIslandIndex = RandomHelper.nextIntFromTo(0, islandCount-1);
		Island island = islands.get(targetIslandIndex); 
		Object toAdd = new Agent(island, energy);
		island.add(toAdd);
		NdPoint pt = island.getSpace().getLocation(toAdd);
		island.getGrid().moveTo(toAdd, (int) pt.getX(), (int) pt.getY());
		return "Agent added to island " + targetIslandIndex + " !";
	}

	@Override
	public int getAgentCount() throws RemoteException {
		return context.size();
	}
}