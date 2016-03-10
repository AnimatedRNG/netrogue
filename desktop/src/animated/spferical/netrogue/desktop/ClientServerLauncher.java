package animated.spferical.netrogue.desktop;

import animated.spferical.netrogue.networking.GameServer;

public class ClientServerLauncher {
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
		
		new DesktopLauncher().main(args);
	}

}
