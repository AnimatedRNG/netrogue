package animated.spferical.netrogue.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import animated.spferical.netrogue.Netrogue;
import animated.spferical.netrogue.networking.GameServer;

public class ClientServerLauncher {
	
	public static void main(String[] args) {
		GameServer server = new GameServer();
		server.start();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Netrogue(), config);
	}

}
