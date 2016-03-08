package animated.spferical.netrogue.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.Gdx;

import animated.spferical.netrogue.networking.GameClient;
import animated.spferical.netrogue.networking.GameServer;

public class TestClientServer {

	@Before
	public void setUp() throws Exception {
		this.server = new GameServer();
		this.client = new GameClient();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test() {
		this.server.start();
		this.client.connect();
		while (true) {
			
			System.out.println(this.client.gameState);
			
			try {
				Thread.sleep((long) ((1f / GameServer.NETWORK_UPDATE_RATE) * 1000));
			} catch (Exception e) {
				Gdx.app.log("Interrupted", e.getMessage());
			}
		}
	}

	private GameServer server;
	private GameClient client;
}
