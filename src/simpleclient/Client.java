package simpleclient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.Node;

public class Client {

    private Client() {}

    public static void main(String[] args) {

        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Node stub = (Node) registry.lookup("NodeServer");
            String response = stub.addAgent(100);
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}