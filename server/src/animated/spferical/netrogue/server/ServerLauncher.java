package animated.spferical.netrogue.server;

import animated.spferical.netrogue.networking.GameServer;

public class ServerLauncher {
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
	}

}
