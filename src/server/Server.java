package server;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import emasrepast.Agent;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;

public class Server implements Node {

	private Context<Object> context;
	private ContinuousSpace<Object> space;
	private Grid <Object> grid;
	
	public Server(Context<Object> context, ContinuousSpace<Object> space, Grid<Object> grid) {
		try {
			this.context = context;
			this.space = space;
			this.grid = grid;
			Node stub = (Node) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("NodeServer", stub);
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
		System.err.println("Server ready");

	}

	public String addAgent(Integer energy) throws RemoteException{
		Object toAdd = new Agent(space, grid, energy);
		context.add(toAdd);
		NdPoint pt = space.getLocation(toAdd);
		grid.moveTo(toAdd, (int) pt.getX(), (int) pt.getY());
		return "Agent added!";
	}
}