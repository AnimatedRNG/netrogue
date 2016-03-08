package animated.spferical.netrogue.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;

import animated.spferical.netrogue.networking.GameClient;
import animated.spferical.netrogue.networking.GameServer;

public class TestClientServer {

	public static final int DURATION = 80;
	public static final int NUMBER_CLIENTS = 10;
	
	@Before
	public void setUp() throws Exception {
		this.server = new GameServer();
		this.clients = new ArrayList<GameClient>();
		for (int i = 0; i < NUMBER_CLIENTS; i++)
			this.clients.add(new GameClient());
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test() {
		this.server.start();
		for (GameClient client : this.clients)
			client.connect();
		
		for (int i = 0; i < DURATION; i++) {
			try {
				Thread.sleep((long) ((1f / GameServer.NETWORK_UPDATE_RATE) * 1000));
			} catch (Exception e) {
				Gdx.app.log("Interrupted", e.getMessage());
			}
		}
	}

	private GameServer server;
	private List<GameClient> clients;
}
