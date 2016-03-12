package animated.spferical.netrogue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.HealingPotion;
import animated.spferical.netrogue.world.Item;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.LevelCacher;
import animated.spferical.netrogue.world.Mob;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.PositionedObject;
import animated.spferical.netrogue.world.Projectile;
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

	Map<Long, Integer> hpCache = new HashMap<>();
	Map<Long, Float> timeSinceInjured = new HashMap<>();

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
		
		if (player != null)
		{
			Level currentLevel = gameState.getLevelByNumber(player.getDungeonLevel());
			
			if (currentLevel.ID != this.levelID)
			{
				this.levelCacher = new LevelCacher(currentLevel);
				this.levelID = currentLevel.ID;
			}
		}
		
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
		Gdx.graphics.setTitle("Netrogue -- FPS: " + 
				Gdx.graphics.getFramesPerSecond());

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

		batch.begin();
		Level level = (Level) gameState.searchChildren(levelID);
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof PositionedObject) {
				if (obj.check("renderLower")) {
					renderObject(obj, delta);
				}
			}
		}
		batch.end();

		batch.begin();

		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof PositionedObject) {
				if (!obj.check("renderLower")) {
					renderObject(obj, delta);
				}
			}
		}

		double closestPlayerDist = Integer.MAX_VALUE;
		Player closestPlayer = null;
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Player && (int) obj.get("level") == (int) level.get("number")) {
				renderObject(obj, delta);
				if (obj.ID != player.ID)
				{
					Player otherPlayer = (Player) obj;
					double dx = player.getX() - otherPlayer.getX();
					double dy = player.getY() - otherPlayer.getY();
					double dist = Math.sqrt(dx * dx + dy * dy);
					if (dist < closestPlayerDist)
					{
						closestPlayerDist = dist;
						closestPlayer = otherPlayer;
					}
				}
			}
		}
		
		if (closestPlayer != null) {
			this.drawArrows(closestPlayer.getX() - player.getX(), 
					closestPlayer.getY() - player.getY(), player.getX(),
					player.getY());
		}
		
		batch.end();
	}

	public void renderObject(NetworkObject obj, float dt) {
		if (!(obj instanceof PositionedObject)) return;
		PositionedObject po = (PositionedObject) obj;
		String type = (String) po.get("type");
		if (po instanceof Item || po instanceof HealingPotion) {
			batch.draw(Assets.items.get(type),
				po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
		} else if (po instanceof Projectile) {
			this.drawProjectile((Projectile) po, dt);
		}
		else {
			if (Assets.animations.containsKey(type))
				batch.draw(Assets.animations.get(type).getKeyFrame(timeElapsed, true),
						po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
			else
				batch.draw(Assets.objects.get(type), 
						po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
			if (obj instanceof Mob || obj instanceof Player) {
				int hp = (int)po.get("hp");
				int maxHP;
				if (obj instanceof Player)
					maxHP = ((Player) obj).calculateMaxHP((int) obj.get("level"));
				else 
					maxHP = (int)po.get("maxHP");

				// update HP cache / timeSinceInjured things
				if (hpCache.containsKey(po.ID)) {
					int oldHP = hpCache.get(po.ID);
					if (hp != oldHP) {
						hpCache.put(po.ID, hp);
						timeSinceInjured.put(po.ID, 0f);
					} else {
						float oldTime = timeSinceInjured.get(po.ID);
						timeSinceInjured.put(po.ID, oldTime + dt);
					}
				} else {
					hpCache.put(po.ID, hp);
					timeSinceInjured.put(po.ID, 1.0f);
				}

				if (timeSinceInjured.get(po.ID) < 0.1f) {
					Color c = batch.getColor();
					batch.setColor(c.r, c.b, c.g, 0.5f);
					batch.draw(Assets.animations.get("redbox").getKeyFrame(timeElapsed, true),
						po.getX() * Constants.tileSize, po.getY() * Constants.tileSize);
					batch.setColor(c.r, c.b, c.g, 1);
				}

				if (obj instanceof Mob && hp < maxHP) {
					drawHealthBar(po.getX(), po.getY(), ((float) hp) / ((float)maxHP));
				}
			}
		}
	}
	
	public void drawProjectile(Projectile projectile, float dt) {
		String name = (String) projectile.get("type");
		
		int theta = (int) projectile.get("theta");
		float deltaX = (((float) projectile.get("actualX")) - projectile.getX());
		float deltaY = (((float) projectile.get("actualY")) - projectile.getY());
		
		if (deltaY != 0 || deltaX != 0)
		{
			theta = (int) (Math.toDegrees(Math.atan2(deltaY, deltaX)));;
			if (theta < 0)
				theta += 360;
		}
			
		name += "ray";
		if ((theta > 22.5 && theta < 67.5) || (theta > 202.5 && theta < 247.5))
			name += "diag2";
		else if ((theta > 112.5 && theta < 157.5) || (theta > 292.5 && theta < 337.5))
			name += "diag1";
		else if ((theta > 67.5 && theta < 112.5) || (theta > 247.5 && theta < 292.5))
			name += "vert";
		else
			name += "horiz";
		
		batch.draw(Assets.animations.get(name).getKeyFrame(timeElapsed, true),
					(float) projectile.getX() * Constants.tileSize, 
					(float) projectile.getY() * Constants.tileSize);
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
	
	public void drawArrows(int offsetX, int offsetY, int centerX, int centerY) {
		if (Math.sqrt(offsetX * offsetX + offsetY * offsetY) < Constants.ARROW_PROXIMITY)
			return;
		
		int radius = (int) (Constants.tileSize * Constants.ARROW_DISTANCE);
		int theta = (int) Math.toDegrees(Math.atan2(offsetY, offsetX));
		if (theta < 0)
			theta += 360;
		
		int x = (int) (centerX * Constants.tileSize + Math.cos(Math.toRadians(theta)) * radius);
		int y = (int) (centerY * Constants.tileSize + Math.sin(Math.toRadians(theta)) * radius);
		
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, 0.5f);
		
		if (theta > 67.5 && theta < 112.5)
			batch.draw(Assets.animations.get("arrowUp").getKeyFrame(timeElapsed, true),
					x, y);
		else if (theta > 247.5 && theta < 292.5)
			batch.draw(Assets.animations.get("arrowDown").getKeyFrame(timeElapsed, true),
					x, y);
		else if (theta < 67.5 || theta > 292.5)
			batch.draw(Assets.animations.get("arrowRight").getKeyFrame(timeElapsed, true),
					x, y);
		else
			batch.draw(Assets.animations.get("arrowLeft").getKeyFrame(timeElapsed, true),
					x, y);
		
		batch.setColor(c.r, c.g, c.b, 1.0f);
		
	}

	public void handleResize(int width, int height) {
		viewport.update(width, height);
	}
}
