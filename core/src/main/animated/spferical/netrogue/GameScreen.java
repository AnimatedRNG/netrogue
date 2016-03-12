package animated.spferical.netrogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.networking.GameClient;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;

public class GameScreen implements Screen {
	private UserInterface ui;
	private GameState gameState;
	private GameClient gameClient;
	private WorldRenderer worldRenderer;
	private Thread logicThread;
	private long lastUpdate;
	private long playerID;

	public GameScreen() {
		ui = new UserInterface();
		gameClient = new GameClient(); 

		gameState = gameClient.blockUntilLoaded();
		Player player = gameClient.findPlayer();
		Level level = gameState.getLevelByNumber(player.getDungeonLevel());
		playerID = player.ID;

		worldRenderer = new WorldRenderer(level, player);
		
		this.lastUpdate = System.currentTimeMillis();
		
		this.logicThread = new Thread(new ClientNetworkHandler());
		this.logicThread.start();
	}

	public void handleKeys(float delta) {
		long newUpdate = System.currentTimeMillis();
		Player player = (Player) gameState.searchChildren(playerID);
		// can't control players that don't exist anymore
		if (player == null) return;
		ClientInputState inputState = new ClientInputState();
		
		if (!ui.isChatFocused()) {
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				inputState.moveUp = true;
			} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
				inputState.moveLeft = true;
			} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				inputState.moveDown = true;
			} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
				inputState.moveRight = true;
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				ui.toggleChatFocus();
			}
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			inputState.stringInput = player.get("name") + ": "
					+ ui.getChatMessage();
			ui.clearChatField();
			ui.toggleChatFocus();
		}
		
		if (inputState.equals(new ClientInputState()))
			return;
		
		this.gameState.handlePlayerInput(player, inputState, (float) (newUpdate - lastUpdate) / 1000f);
		this.sendInputToServer(inputState);
		this.lastUpdate = newUpdate;
	}

	@Override
	public void show() {
        Gdx.input.setInputProcessor(ui.stage);
	}

	@Override
	public void render(float delta) {
		if (gameState.searchChildren(playerID) == null) {
			Player p = gameClient.findPlayer();
			if (p != null) {
				playerID = p.ID;
			}
		}
		gameState = gameClient.currentGameState;
		worldRenderer.render(gameState, delta);
		ui.draw(gameState, playerID);
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
		gameClient.sendObjectToServer(input);
		
		input.resetAll();
		
		Log.info("Sent ClientInputState to server");
	}
	
	public class ClientNetworkHandler implements Runnable {

		public boolean running = true;
		
		@Override
		public void run() {
			while (running) {
				gameState.updateAllChildren(gameState, ((float) (System.currentTimeMillis() 
						- (Long) gameState.get("lastTimeUpdate")) / 1000f));
				
				try {
					Thread.sleep((long) (((float) GameClient.CLIENT_LOGIC_UPDATE_RATE / 60f) * 1000L));
				} catch (InterruptedException e) {
					Log.error("Client Networking", "Interrupted", e);
				}
			}
		}		
	}
}
