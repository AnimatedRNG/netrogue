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
	Level level;
	SpriteBatch batch;
	WorldRenderer worldRenderer;
	float timeSinceLastAction = 0;
	final float actionDelay = 0.1f; // in seconds

	public GameScreen() {
		ui = new UserInterface();
		batch = new SpriteBatch();
		gameClient = new GameClient(); 

		// TODO: get stuff from server instead
		gameState = gameClient.blockUntilConnected();
		level = new Level(1, MapGenerator.mapHeight,
				MapGenerator.mapWidth);
		gameState.putChild(level);
		MapGenerator.generateMap(level);
		int mapCenterX = MapGenerator.mapWidth * 16 / 2;
		int mapCenterY = MapGenerator.mapHeight * 16 / 2;
		player = new Player(mapCenterX, mapCenterY);
		level.putChild(player);

		worldRenderer = new WorldRenderer(level, player);

	}

	public void handleKeys(float delta) {
		timeSinceLastAction += delta;
		if (timeSinceLastAction > actionDelay) {
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
		gameState = gameClient.currentGameState;
		worldRenderer.render(delta);
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
