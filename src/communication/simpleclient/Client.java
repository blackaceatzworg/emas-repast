package communication.simpleclient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import communication.Node;
import communication.config.ConfigReader;


public class Client {

    private Client() {}

    //adds Agent to every host
    public static void main(String[] args) {

        String rmiHost = ConfigReader.getRmiHost();
        Integer rmiPort = ConfigReader.getRmiPort();
        List <String> hosts = ConfigReader.getHosts();

        try {
            for (String h : hosts){
                Registry registry = LocateRegistry.getRegistry(rmiHost, rmiPort);
                Node stub = (Node) registry.lookup("NodeServer"+h);
                String response = stub.addAgent(100);
                System.out.println("response: " + response);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}