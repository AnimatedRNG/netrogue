package animated.spferical.netrogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.Tile;

public class GameScreen implements Screen {
	float timeElapsed;
	UserInterface ui;
	Viewport viewport;
	Camera camera;
	GameState gameState;
	Player player;
	Level level;
	SpriteBatch batch;

	public GameScreen() {
		ui = new UserInterface();
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(1024, 768, camera);
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

	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		timeElapsed += delta;
		// move camera to player
		camera.position.x = player.getX() * 64 + 32;
		camera.position.y = player.getY() * 64 + 32;
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		int playerChunkRow = player.getY() / 16;
		int playerChunkCol = player.getX() / 16;
		// draw tiles
		for (int row = playerChunkRow - 1; row <= playerChunkRow + 1; row++) {
			for (int col = playerChunkCol - 1; col <= playerChunkCol + 1; col++) {
				Chunk chunk = level.getChunk(row, col);
				if (chunk == null) {
					continue;
				}
				Tile.Type[][] tiles = (Tile.Type[][]) chunk.get("tiles");
				for (int tileRow = 0; tileRow < tiles.length; tileRow++) {
					for (int tileCol = 0; tileCol < tiles[0].length; tileCol++) {
						int x = chunk.getCol() * 64 * 16 + tileCol * 64;
						int y = chunk.getRow() * 64 * 16 + tileRow * 64;
						TextureRegion tileTexture;
						switch (tiles[tileRow][tileCol]) {
						case FLOOR:
							tileTexture = Assets.loadTextureRegion("DawnLike/Objects/Floor.png", 1, 1);
							break;
						default:
						case WALL:
							tileTexture = Assets.loadTextureRegion("DawnLike/Objects/Wall.png", 3, 3);
							break;
						}
						batch.draw(tileTexture, x, y);
					}
				}
			}
		}
		batch.end();

		// TODO: draw player
		batch.begin();
		batch.draw(Assets.loadAnimationFromBasePath("DawnLike/Characters/Player", 0, 0).getKeyFrame(timeElapsed, true),
				player.getX() * 64, player.getY() * 64);
		batch.end();

		ui.draw(camera);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
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
