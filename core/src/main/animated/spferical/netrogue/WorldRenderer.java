package animated.spferical.netrogue;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.Tile;

public class WorldRenderer {
	float timeElapsed;
	Level level;
	Camera camera;
	SpriteBatch batch;
	Viewport viewport;
	Player player;
	EnumMap<Tile.Type, TextureRegion> tileTextureRegions;

	public WorldRenderer(Level level, Player player) {
		this.level = level;
		this.player = player;
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(1024, 768, camera);
		batch = new SpriteBatch();
		loadTileTextureRegions();
	}

	public void loadTileTextureRegions() {
		tileTextureRegions = new EnumMap<>(Tile.Type.class);
		tileTextureRegions.put(Tile.Type.WALL,
				Assets.loadTextureRegion("DawnLike/Objects/Wall.png", 3, 3));
		tileTextureRegions.put(Tile.Type.FLOOR,
				Assets.loadTextureRegion("DawnLike/Objects/Floor.png", 13, 1));
	}

	public void updateCamera(float delta) {
		float dx = player.getX() * 64 + 32 - camera.position.x;
		float dy = player.getY() * 64 + 32 - camera.position.y;
		camera.position.x += dx * delta * 10;
		camera.position.y += dy * delta * 10;
		camera.update();
	}

	public void render(float delta) {
		timeElapsed += delta;
		updateCamera(delta);
		// move camera to player
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
						TextureRegion tileTexture = tileTextureRegions.get(
								tiles[tileRow][tileCol]);
						batch.draw(tileTexture, x, y);
					}
				}
			}
		}
		batch.end();

		batch.begin();
		batch.draw(Assets.animations.get(0).getKeyFrame(timeElapsed, true),
				player.getX() * 64, player.getY() * 64);
		batch.end();
	}

	public void handleResize(int width, int height) {
		viewport.update(width, height, true);
	}
}
