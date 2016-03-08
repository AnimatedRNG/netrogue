package animated.spferical.netrogue.networking;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;

public class GameServer extends Listener implements Runnable {

	public static final float NETWORK_UPDATE_RATE = 10f;
	public static final int PORT_NUMBER = 37707;
	
	public GameServer() {
		this.server = new Server();
		this.threaded = new ThreadedListener(this);
		this.server.addListener(this.threaded);
		this.oldGameState = new GameState();
		this.gameState = (GameState) this.oldGameState.clone();
		this.isRunning = true;
		this.networkingThead = new Thread(this);
		Registrar.register(server.getKryo());
		
		this.playerIDs = new HashMap<>();
	}
	
	public void start() {
		this.networkingThead.start();
		
		try {
			server.start();
			server.bind(PORT_NUMBER);
		} catch (IOException e) {
			Gdx.app.error("Networking", "Error binding to port", e);
		}
	}
	
	@Override
	public void run() {
		while (this.isRunning) {
			// Get all diffs in the GameState object
			// and send them to everybody
			List<Diff> diffs = DiffGenerator.generateDiffs(this.oldGameState, this.gameState);
			
			// Actually implement a filter function
			
			// Send everyone diffs
			for (Connection connection : this.playerIDs.keySet())
				this.sendUpdateToPlayer(diffs, connection);
			
			this.oldGameState = (GameState) this.gameState.clone();
			
			try {
				Thread.sleep((long) ((1f / NETWORK_UPDATE_RATE) * 1000));
			} catch (Exception e) {
				Gdx.app.log("Interrupted", e.getMessage());
			}
		}
	}
	
	@Override
	public void connected(Connection connection) {
		// Player connected, create new Player object
		Player player = new Player();
		player.put("connection", connection);
		this.playerIDs.put(connection, player.ID);
		this.gameState.putChild(player);
		
		this.server.sendToTCP(connection.getID(),
			new InfoResponse(this.gameState));
	}
	
	@Override
	public void disconnected(Connection connection) {
		// Player disconnected, destroy Player object
		this.gameState.removeChild(this.playerIDs.remove(connection));
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
			}
		}
	}
	
	@Override
	public void idle(Connection connection) {
		// Not much is happening
	}
	
	private void sendUpdateToPlayer(List<Diff> diffs, Connection player) {
		Player p = (Player) this.gameState.getChild(this.playerIDs.get(player));
		
		// Filter function here
		
		for (Diff diff : diffs)
			this.server.sendToTCP(player.getID(), diff);
	}

	private Server server;
	private Thread networkingThead;
	private ThreadedListener threaded;
	private GameState gameState;
	private GameState oldGameState;
	private boolean isRunning;
	
	private HashMap<Connection, Long> playerIDs;
}
