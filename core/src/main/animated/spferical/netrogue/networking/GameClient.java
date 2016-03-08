package animated.spferical.netrogue.networking;

import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.world.GameState;

public class GameClient extends Listener {
	
	public static final int TIMEOUT = 1000; 

	public GameState currentGameState;
	public GameState oldGameState;
	
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
	
	public void disconnect() {
		client.close();
	}
	
	@Override
	public void connected(Connection connection) {
		
	}
	
	@Override
	public void disconnected(Connection connection) {
		
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof InfoResponse)
		{
			NetworkObject networkObject = ((InfoResponse) object).response;
			if (networkObject instanceof GameState)
			{
				this.currentGameState = (GameState) networkObject;
				this.oldGameState = (GameState) this.currentGameState.clone();
				Log.info("Directly updated gamestate: " + this.currentGameState);
			}
		}
		else if (object instanceof List)
		{
			@SuppressWarnings("unchecked")
			List<Object> objectList = ((List<Object>) object);
			
			for (Object obj : objectList)
			{
				if (obj instanceof Diff)
				{
					Diff diff = ((Diff) obj);
					
					if (diff.connectionID != connection.getID())
					{
						Log.warn("Client Networking", "Diff reports invalid connection. Ignoring.");
						return;
					}
					
					boolean result = diff.apply(this.oldGameState);
					if (!result)
					{
						Log.error("Client Networking", "Failed to apply diff " + obj);
					}
					else
					{
						Log.info("Client Networking", "Applied Diff " + diff.newUpdate);
					}
				}
			}
			this.currentGameState = (GameState) this.oldGameState.clone();
		}
	}
	
	@Override
	public void idle(Connection connection) {
		
	}

	private Client client;
	private ThreadedListener threaded;
}
