package animated.spferical.netrogue.networking;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import animated.spferical.netrogue.world.GameState;

public class GameServer extends Listener implements Runnable {

	public static final float NETWORK_UPDATE_RATE = 10f;
	
	public GameServer() {
		this.server = new Server();
		this.threaded = new ThreadedListener(this);
		this.oldGameState = new GameState();
		this.gameState = (GameState) this.oldGameState.clone();
		this.isRunning = true;
		this.networkingThead = new Thread(this);
	}
	
	@Override
	public void run() {
		while (this.isRunning) {
			// Get all diffs in the GameState object
			// and send them to everybody
			
			List<Diff> diffs = DiffGenerator.generateDiffs(this.oldGameState, this.gameState);
			
			// Send the diffs to everyone
			
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
	}
	
	@Override
	public void disconnected (Connection connection) {
		// Player disconnected, delete the Player object
	}
	
	@Override
	public void received(Connection connection, Object object) {
		// Received object. Update game state
	}
	
	@Override
	public void idle (Connection connection) {
		// Not much is happening
	}

	private Server server;
	private Thread networkingThead;
	private ThreadedListener threaded;
	private GameState gameState;
	private GameState oldGameState;
	private boolean isRunning;
}
