package animated.spferical.netrogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.networking.GameClient;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;

public class GameScreen implements Screen {
	UserInterface ui;
	GameState gameState;
	GameClient gameClient;
	SpriteBatch batch;
	WorldRenderer worldRenderer;
	Thread networkThread;

	public GameScreen() {
		ui = new UserInterface();
		batch = new SpriteBatch();
		gameClient = new GameClient(); 

		gameState = gameClient.blockUntilLoaded();
		Player player = gameClient.findPlayer();
		Level level = gameState.getLevelByNumber(player.getDungeonLevel());

		worldRenderer = new WorldRenderer(level, player);
		
		this.networkThread = new Thread(new ClientNetworkHandler());
		this.networkThread.start();
	}

	public void handleKeys(float delta) {
		Player player = gameClient.findPlayer();
		ClientInputState inputState = (ClientInputState) player.get("input");
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
			inputState.moveUp = true;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			inputState.moveLeft = true;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
			inputState.moveDown = true;
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			inputState.moveRight = true;
		}
		
		player.put("input", inputState);
		this.gameState.handlePlayerInput(player, delta);
		this.sendInputToServer(inputState);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		gameState = gameClient.currentGameState;
		worldRenderer.render(gameState, delta);
		ui.draw();
		handleKeys(delta);
	}

	@Override
	public void resize(int width, int height) {
		ui.handleResize(width, height);
		worldRenderer.handleResize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		this.gameClient.disconnect();
	}
	
	public void sendInputToServer(ClientInputState input) {
		Player player = gameClient.findPlayer();
		gameClient.sendObjectToServer(input);
		
		input.resetAll();
		player.put("input", input);
		
		Log.info("Sent ClientInputState to server");
	}
	
	public class ClientNetworkHandler implements Runnable {

		public boolean running = true;
		
		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep((long) (((float) GameClient.CLIENT_NETWORK_UPDATE_RATE / 60f) * 1000L));
				} catch (InterruptedException e) {
					Log.error("Client Networking", "Interrupted", e);
				}
			}
		}		
	}
}
