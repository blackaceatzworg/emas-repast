package communication.simpleclient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import communication.Node;
import communication.config.ConfigReader;
import communication.server.NodesContainer;


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
                NodesContainer stub = (NodesContainer) registry.lookup("RegistryContainer");
                
                Node node = stub.getNodeByName("NodeServer"+h);
                int before = node.getAgentCount();
                String response = node.addAgent(100, 100);
                int after = node.getAgentCount();
                System.out.println("agents on node: " + before);
                System.out.println("response: " + response);
                System.out.println("agents on node: " + after);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}