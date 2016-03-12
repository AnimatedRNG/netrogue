package animated.spferical.netrogue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Item;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.LevelCacher;
import animated.spferical.netrogue.world.Mob;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.PositionedObject;
import animated.spferical.netrogue.world.Tile;
import animated.spferical.netrogue.world.TileTypeArray;

public class WorldRenderer {
	float timeElapsed;
	LevelCacher levelCacher;
	Camera camera;
	SpriteBatch batch;
	Viewport viewport;
	long playerID;
	EnumMap<Tile.Type, TextureRegion> tileTextureRegions;
	long levelID;

	public WorldRenderer(Level level, Player player) {
		this.levelID = level.ID;
		this.levelCacher = new LevelCacher(level);
		this.playerID = player.ID;
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

	public void updateCamera(Player player, float delta) {
		int tileSize = Constants.tileSize;
		float dx = tileSize * (player.getX() + .5f) - camera.position.x;
		float dy = tileSize * (player.getY() + .5f) - camera.position.y;
		camera.position.x += dx * delta * 10;
		camera.position.y += dy * delta * 10;
		camera.update();
	}

	public void render(GameState gameState, float delta) {
		Player player = (Player) gameState.searchChildren(playerID);
		int tileSize = Constants.tileSize;
		int chunkSize = Constants.chunkSize;
		timeElapsed += delta;
		if (player != null)
			updateCamera(player, delta);
		// move camera to player
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		if (player == null) {
			// TODO: display death message
		}

		int playerChunkRow = (int) (camera.position.y / Constants.tileSize / Constants.chunkSize);
		int playerChunkCol = (int) (camera.position.x / Constants.tileSize / Constants.chunkSize);
		// draw tiles
		for (int row = playerChunkRow - 1; row <= playerChunkRow + 1; row++) {
			for (int col = playerChunkCol - 1; col <= playerChunkCol + 1; col++) {
				Chunk chunk = levelCacher.getChunk(gameState, row, col);
				if (chunk == null) {
					continue;
				}
				TileTypeArray tiles = (TileTypeArray) chunk.get("tiles");
				for (int tileRow = 0; tileRow < tiles.tiles.length; tileRow++) {
					for (int tileCol = 0; tileCol < tiles.tiles[0].length; tileCol++) {
						int tileX = chunk.getCol() * chunkSize + tileCol;
						int tileY = chunk.getRow() * chunkSize + tileRow;
						int x = tileSize * tileX;
						int y = tileSize * tileY;
						TextureRegion tileTexture = tileTextureRegions.get(
								tiles.tiles[tileRow][tileCol]);
						batch.draw(tileTexture, x, y);
					}
				}
			}
		}
		batch.end();

		List<NetworkObject> objToRenderOnTop = new ArrayList<>();

		batch.begin();
		Level level = (Level) gameState.searchChildren(levelID);
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof PositionedObject) {
				if (!obj.check("renderLower")) {
					objToRenderOnTop.add(obj);
					continue;
				}
				renderObject(obj);
			}
		}
		batch.end();

		batch.begin();

		for (NetworkObject obj : objToRenderOnTop) {
			renderObject(obj);
		}

		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Player) {
				renderObject(obj);
			}
		}
		batch.end();
	}

	public void renderObject(NetworkObject obj) {
		if (!(obj instanceof PositionedObject)) return;
		PositionedObject po = (PositionedObject) obj;
		String type = (String) po.get("type");
		if (po instanceof Item) {
			batch.draw(Assets.items.get(type),
				po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
		} else {
			batch.draw(Assets.animations.get(type).getKeyFrame(timeElapsed, true),
					po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
			if (obj instanceof Mob) {
				int hp = (int)po.get("hp");
				int maxHP = (int)po.get("maxHP");
				if (hp < maxHP) {
					drawHealthBar(po.getX(), po.getY(), ((float) hp) / ((float)maxHP));
				}
			}
		}
	}

	public void drawHealthBar(int x, int y, float healthFraction) {
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, 0.6f);
		x = x * Constants.tileSize;
		y = y * Constants.tileSize;
		if (healthFraction > .75) {
			batch.draw(Assets.animations.get("hpfull").getKeyFrame(timeElapsed), x, y);
		} else if (healthFraction > .5) {
			batch.draw(Assets.animations.get("hp3").getKeyFrame(timeElapsed), x, y);
		} else if (healthFraction > .25) {
			batch.draw(Assets.animations.get("hp2").getKeyFrame(timeElapsed), x, y);
		} else {
			batch.draw(Assets.animations.get("hp1").getKeyFrame(timeElapsed), x, y);
		}
		batch.setColor(c.r, c.g, c.b, 1f);
	}

	public void handleResize(int width, int height) {
		viewport.update(width, height);
	}
}
