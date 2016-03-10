package animated.spferical.netrogue.networking;

import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;

public class GameClient extends Listener {
	
	public static final int TIMEOUT = 10000;
	public static final int LOAD_TIMEOUT = 10000;
	public static final int BLOCKING_PERIOD = 10;
	public static final int[] BUFFER_SIZES = {131072 * 8, 131072 * 8};

	public GameState currentGameState;
	public GameState oldGameState;
	
	public GameClient() {
		this.client = new Client(BUFFER_SIZES[0], BUFFER_SIZES[1]);
		this.client.start();
		this.threaded = new ThreadedListener(this);
		this.client.addListener(this.threaded);
		Registrar.register(this.client.getKryo());
	}

	public int getConnectionID() {
		return this.client.getID();
	}
	
	public void connect() {
		try {
			// 18.189.109.162
			this.client.connect(TIMEOUT, "localhost", GameServer.PORT_NUMBER);
		} catch (IOException e) {
			Gdx.app.error("Networking", "Failed to connect to server", e);
		}
	}
	
	public GameState blockUntilConnected() {
		this.connect();
		int accumulator = 0;
		while (this.currentGameState == null && accumulator < TIMEOUT)
		{
			try {
				Thread.sleep(BLOCKING_PERIOD);
			} catch (InterruptedException e) {
				Log.error("Interrupted", e);
			}
			accumulator += BLOCKING_PERIOD;
		}
		return this.currentGameState;
	}
	
	/**
	 * Blocks until client is ready to join the game world
	 */
	public GameState blockUntilLoaded() {
		this.blockUntilConnected();
		Log.info("Client Networking", "Connected");
		int accumulator = 0;
		while (this.findPlayer() == null && accumulator < LOAD_TIMEOUT)
		{
			try {
				Thread.sleep(BLOCKING_PERIOD);
			} catch (InterruptedException e) {
				Log.error("Interrupted", e);
			}
			accumulator += BLOCKING_PERIOD;
		}
		if (accumulator > LOAD_TIMEOUT)
			Log.error("Client Networking", "Unable to load world from server!");
		else
			Log.info("Client Networking", "Loaded world from server in "
					+ (float) accumulator / 1000f + " seconds");
		return this.currentGameState;
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
			else
			{
				long parent = networkObject.parent;
				if (this.oldGameState == null)
				{
					Log.error("Client Networking", 
							"Received InfoResponse but haven't yet received GameState!");
					return;
				}
				else
				{
					Log.info("Received an InfoResponse of " + networkObject);
				}
				this.oldGameState.searchChildren(parent).putChild(networkObject);
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
	
	public Player findPlayer() {
		for (NetworkObject obj : this.currentGameState.getAllChildren().values()) {
			if (obj instanceof Player) {
				Player player = (Player) obj;
				int connectionID = player.getConnectionID();
				if (connectionID == this.getConnectionID()) {
					return player;
				}
			}
		}
		return null;
	}
	
	@Override
	public void idle(Connection connection) {
		
	}

	private Client client;
	private ThreadedListener threaded;
}
