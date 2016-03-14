package animated.spferical.netrogue.networking;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ChatNetworkObject;
import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.MapGenerator;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.Spawner;

public class GameServer extends Listener implements Runnable {

	public static final float NETWORK_UPDATE_RATE = 10F;
	public static final int PORT_NUMBER = 37707;
	
	public static final int[] BUFFER_SIZES = {131072 * 8, 131072 * 8};
	Spawner spawner = new Spawner();
	
	public GameServer() {
		this.server = new Server(BUFFER_SIZES[0], BUFFER_SIZES[1]);
		this.threaded = new ThreadedListener(this);
		this.server.addListener(this.threaded);
		this.oldGameState = new GameState();
		this.oldGameState.setupGame();
		this.gameState = (GameState) this.oldGameState.clone();
		this.isRunning = true;
		this.networkingThead = new Thread(this);
		Registrar.register(server.getKryo());
		
		this.playerIDs = new HashMap<>();
		this.unverifiedPlayers = new HashMap<>();
		this.timeSinceLastMove = new HashMap<>();
	}

	
	public void start() {

		this.networkingThead.start();
		
		Log.info("Server Networking", "Server started. GameState ID: " + this.gameState.ID);
		
		try {
			server.start();
			server.bind(PORT_NUMBER);
		} catch (IOException e) {
			Log.error("Server Networking", "Error binding to port", e);
		}
	}

	@Override
	public void run() {
		while (this.isRunning) {
			this.gameState.updateAllChildren(this.gameState, 
					((float) (System.currentTimeMillis() - (Long) gameState.get("lastTimeUpdate"))) / 1000f);
			this.gameState.put("lastTimeUpdate", System.currentTimeMillis());
			this.spawner.spawnMobs(this.gameState);
			this.spawner.spawnItems(this.gameState);
			
			// Get all diffs in the GameState object
			// and send them to everybody
			synchronized (this.gameState) 
			{
				List<Diff> diffs = DiffGenerator.generateDiffs(this.oldGameState, this.gameState);
			
				// Send everyone diffs
				for (Connection connection : this.playerIDs.keySet())
					this.sendUpdateToPlayer(diffs, connection);
				
				this.oldGameState = (GameState) this.gameState.clone();
			}
			
			try {
				Thread.sleep((long) ((1f / NETWORK_UPDATE_RATE) * 1000));
			} catch (Exception e) {
				Log.error("Server Networking", "Interrupted", e);
			}
		}
	}
	
	@Override
	public void connected(Connection connection) {
		this.sendRecursiveInfoToPlayer(connection, this.oldGameState);
		
		// Player connected, create new Player object
		int mapCenterX = MapGenerator.mapWidth * Constants.chunkSize / 2;
		int mapCenterY = MapGenerator.mapHeight * Constants.chunkSize / 2;
		Player player = new Player(connection, mapCenterX, mapCenterY);
		player.randomlyAssignName();
		
		this.playerIDs.put(connection, player.ID);
		this.unverifiedPlayers.put(connection, player.ID);
		this.timeSinceLastMove.put(connection, System.currentTimeMillis());
		this.gameState.putChild(player);
		
		Log.info("Server Networking", "Player " + connection.getID() + " connected with ID: " + player.ID);
		
		this.gameState.announce("Player " + player.get("name") + " has joined the dungeon.");
	}
	
	@Override
	public void disconnected(Connection connection) {
		// Player disconnected, destroy Player object
		Long id = this.playerIDs.remove(connection);
		NetworkObject obj = this.gameState.getChild(id);
		if (obj != null) {
			this.gameState.announce("Player " + this.gameState.getChild(id).get("name") +
					" has left the dungeon.");
		}
		this.gameState.removeChild(id);
		this.timeSinceLastMove.remove(connection);
	}
	
	@Override
	public void received(Connection connection, Object object) {
		if (object instanceof InfoQuery)
		{
			InfoQuery queryObj = (InfoQuery) object;
			if (queryObj.queryID != null)
			{
				NetworkObject target = this.gameState.searchChildren((queryObj).queryID);
				if (target != null)
					this.server.sendToTCP(connection.getID(), new InfoResponse(target));
				Log.info("Server Networking", "Client requested object " + target);
			}
		}
		else if (object instanceof ClientInputState)
		{
			Player player = (Player) this.gameState.getChild(this.playerIDs.get(connection));
			
			if (this.unverifiedPlayers.containsKey(connection))
			{
				Log.info("Server Networking", "Received input from player of unknown version");
				ClientInputState obj = (ClientInputState) object;
				if (obj.inputType == null || 
						obj.inputType != ClientInputState.InputType.REPORT_VERSION ||
						obj.intInput != Constants.VERSION)
				{
					// Player is running an invalid version. Start harrassing them.
					player.addPlayerMessage("This server is on version " + Constants.VERSION_STRING
							+ "; please update your client.");
				}
				else
				{
					this.unverifiedPlayers.remove(connection);
					Log.info("Server Networking", "Player successfully verified their game version");
				}
			}
			
			long delta = System.currentTimeMillis() - timeSinceLastMove.get(connection);
			this.gameState.handlePlayerInput(player, (ClientInputState) object, (float) delta / 1000f);
			this.timeSinceLastMove.put(connection, System.currentTimeMillis());
			
			//Log.info("Server Networking", "Player " + player + 
			//		" is now at position (" + player.getX() + ", " + player.getY() + ")");
		}
	}
	
	@Override
	public void idle(Connection connection) {
		// Not much is happening
	}
	
	private void sendRecursiveInfoToPlayer(Connection connection, NetworkObject object) {
		//this.server.sendToTCP(connection.getID(),
		//		new InfoResponse(object));
		
		NetworkObject tempObject = object.clone();
		tempObject.killChildren();
		this.server.sendToTCP(connection.getID(), 
				new InfoResponse(tempObject));
		
		// Recursive child genocide and cloning
		Iterator<Entry<Long, NetworkObject>> children = 
				object.getAllChildren().entrySet().iterator();
		while (children.hasNext())
		{
			NetworkObject child = children.next().getValue().clone();
			child.killChildren();
			this.server.sendToTCP(connection.getID(), 
				new InfoResponse(child));
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				Log.info("Server Networking", "Interrupted", e);
			}
		}
		
		children = object.getAllChildren().entrySet().iterator();
		while (children.hasNext())
			sendRecursiveInfoToPlayer(connection, children.next().getValue());
	}
	
	private void sendUpdateToPlayer(List<Diff> diffs, Connection player) {
		Player p = (Player) this.gameState.getChild(this.playerIDs.get(player));
		
		// Filter function here
		for (Diff diff : diffs)
			diff.connectionID = player.getID();
		
		if (!diffs.isEmpty())
			this.server.sendToTCP(player.getID(), diffs);
	}

	private Server server;
	private Thread networkingThead;
	private ThreadedListener threaded;
	private GameState gameState;
	private GameState oldGameState;
	private boolean isRunning;
	
	private HashMap<Connection, Long> unverifiedPlayers;
	private HashMap<Connection, Long> playerIDs;
	
	// Connection -> Time since last move
	private HashMap<Connection, Long> timeSinceLastMove;
}
