package animated.spferical.netrogue.networking;

import java.io.IOException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;

public class GameClient extends Listener {
	
	public static final int TIMEOUT = 10 * 1000;
	public static final int LOAD_TIMEOUT = 60 * 60 * 1000;
	public static final int BLOCKING_PERIOD = 10;
	
	// Try to keep this value close to the server update rate
	public static final int CLIENT_LOGIC_UPDATE_RATE = 10;
	public static final int[] BUFFER_SIZES = {131072, 131072};

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
			this.client.connect(TIMEOUT, "netrogue.mgpfe.me", GameServer.PORT_NUMBER);
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
		client.updateReturnTripTime();
		ClientInputState reportVersion = new ClientInputState();
		reportVersion.inputType = ClientInputState.InputType.REPORT_VERSION;
		reportVersion.intInput = Constants.VERSION;
		this.client.sendTCP(reportVersion);
		Log.info("Client Networking", "Reporting version to server");
	}
	
	@Override
	public void disconnected(Connection connection) {
		
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof Ping)
		{
			Ping ping = (Ping) object;
			//if (ping.isReply)
			//	Log.info("One-way ping time of " + connection.getReturnTripTime() / 2);
			client.updateReturnTripTime();
		}
		else if (object instanceof InfoResponse)
		{
			NetworkObject networkObject = ((InfoResponse) object).response;
			if (networkObject instanceof GameState)
			{
				this.currentGameState = (GameState) networkObject;
				this.currentGameState.put("lastTimeUpdate", 
						System.currentTimeMillis() + connection.getReturnTripTime() / 2);
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
					
					this.handleDiff(diff, connection);
				}
			}
			this.oldGameState.put("lastTimeUpdate", 
					System.currentTimeMillis() + connection.getReturnTripTime() / 2);
			
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
	
	public synchronized void sendObjectToServer(Object object) {
		this.client.sendTCP(object);
	}
	
	@Override
	public void idle(Connection connection) {
		
	}
	
	private void handleDiff(Diff diff, Connection connection) {
		if (diff.connectionID != connection.getID())
		{
			Log.warn("Client Networking", "Diff reports invalid connection. Ignoring.");
			return;
		}
		
		Player player = this.findPlayer();
		if (player != null && diff instanceof ModifyAttributeDiff && 
				((ModifyAttributeDiff) diff).targetID == player.ID)
		{
			if (((ModifyAttributeDiff) diff).name.equals("x"))
			{
				diff = savePlayer((ModifyAttributeDiff) diff, player.getX(), player);
			}
			else if (((ModifyAttributeDiff) diff).name.equals("y"))
			{
				diff = savePlayer((ModifyAttributeDiff) diff, player.getY(), player);
			}
		}
		
		boolean result = diff.apply(this.oldGameState);
		if (!result)
		{
			Log.error("Client Networking", "Failed to apply diff " + diff);
		}
	}
	
	private Diff savePlayer(ModifyAttributeDiff diff, Integer value, Player player) {
		// If the server argues with the client about its position
		// and the client isn't too wrong, we don't do anything on 
		// the client
		if (Math.abs((float) value - (int) diff.value) < 0)
		{
			diff.actuallyDoSomething = false;
		}
		return diff;
	}

	private Client client;
	private ThreadedListener threaded;
}
