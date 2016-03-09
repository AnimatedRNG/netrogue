package animated.spferical.netrogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import animated.spferical.netrogue.networking.GameClient;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;

public class GameScreen implements Screen {
	UserInterface ui;
	GameState gameState;
	GameClient gameClient;
	Player player;
	SpriteBatch batch;
	WorldRenderer worldRenderer;
	float timeSinceLastAction = 0;

	public GameScreen() {
		ui = new UserInterface();
		batch = new SpriteBatch();
		gameClient = new GameClient(); 

		//gameState = gameClient.blockUntilLoaded();
		gameState = new GameState();
		gameState.setupGame();

		int mapCenterX = MapGenerator.mapWidth * Constants.chunkSize / 2;
		int mapCenterY = MapGenerator.mapHeight * Constants.chunkSize / 2;

		player = new Player(mapCenterX, mapCenterY);
		gameState.putChild(new Player(mapCenterX, mapCenterY));
		//player = gameClient.findPlayer();
		Level level = getCurrentLevel();

		worldRenderer = new WorldRenderer(level, player);
	}

	public Level getCurrentLevel() {
		return gameState.findLevel(player.getDungeonLevel());
	}

	public void handleKeys(float delta) {
		timeSinceLastAction += delta;
		if (timeSinceLastAction > Constants.actionDelay) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
				player.setY(player.getY() + 1);
				timeSinceLastAction = 0;
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
				player.setX(player.getX() - 1);
				timeSinceLastAction = 0;
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
				player.setY(player.getY() - 1);
				timeSinceLastAction = 0;
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				player.setX(player.getX() + 1);
				timeSinceLastAction = 0;
			}
		}
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		//gameState = gameClient.currentGameState;

		// re-find player in case object changed
		//player = gameClient.findPlayer();

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
	}
}
