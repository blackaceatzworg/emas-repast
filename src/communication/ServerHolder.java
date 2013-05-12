package communication;

public class ServerHolder {
	private static Server server;

	public static Server getServer() {
		return server;
	}

	public static void setServer(Server server) {
		ServerHolder.server = server;
	}
	
}
