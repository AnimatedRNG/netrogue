package animated.spferical.netrogue;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;

public class GameScreen implements Screen {
	UserInterface ui;
	GameState gameState;
	Player player;
	Level level;
	SpriteBatch batch;
	WorldRenderer worldRenderer;

	public GameScreen() {
		ui = new UserInterface();
		batch = new SpriteBatch();

		// TODO: get stuff from server instead
		gameState = new GameState();
		level = new Level(gameState, 1,
				MapGenerator.mapHeight, MapGenerator.mapWidth);
		gameState.putChild(level);
		MapGenerator.generateMap(level);
		int mapCenterX = MapGenerator.mapWidth * 16 / 2;
		int mapCenterY = MapGenerator.mapHeight * 16 / 2;
		player = new Player(level, mapCenterX, mapCenterY);
		level.putChild(player);

		worldRenderer = new WorldRenderer(level, player);

	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		worldRenderer.render(delta);
		ui.draw();
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
