package animated.spferical.netrogue.networking;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import animated.spferical.netrogue.world.GameState;

public class GameClient extends Listener {
	
	public static final int TIMEOUT = 1000; 

	public GameState gameState;
	
	public GameClient() {
		this.client = new Client();
		this.client.start();
		this.threaded = new ThreadedListener(this);
		this.client.addListener(this.threaded);
		Registrar.register(this.client.getKryo());
	}
	
	public void connect() {
		try {
			this.client.connect(TIMEOUT, "localhost", GameServer.PORT_NUMBER);
		} catch (IOException e) {
			Gdx.app.error("Networking", "Failed to connect to server", e);
		}
	}
	
	@Override
	public void connected(Connection connection) {
		
	}
	
	@Override
	public void disconnected(Connection connection) {
		
	}
	
	@Override
	public void received(Connection connection, Object object) {
		System.out.println("SOMETHING WAS RECEIVED!");
		if (object instanceof InfoResponse)
		{
			NetworkObject networkObject = ((InfoResponse) object).response;
			if (networkObject instanceof GameState)
				this.gameState = (GameState) networkObject;
		}
	}
	
	@Override
	public void idle(Connection connection) {
		
	}

	private Client client;
	private ThreadedListener threaded;
}
