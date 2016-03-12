package animated.spferical.netrogue.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.networking.NetworkObject;

public class Spawner {
	Random random;

	class MobType {
		public String name;
		public int maxHP;
		public int damage;
		public int XP;
		public float moveSpeed;
		public float attackSpeed;

		public MobType(String name, int maxHP, int XP, int damage,
				float moveSpeed, float attackSpeed) {
			this.name = name;
			this.maxHP = maxHP;
			this.damage = damage;
			this.moveSpeed = moveSpeed;
			this.attackSpeed = attackSpeed;
			this.XP = XP;
		}
	};

	final MobType[] mobTypes = {
		// new MobType("name", HP, XP, damage, moveSpeed, attackSpeed);
		new MobType("worm", 10, 1, 1, 1, 1),
		new MobType("ant", 7, 2, 1, .4f, .4f),
		new MobType("beetle", 6, 1, 1, .75f, .5f),
		new MobType("butterfly", 5, 1, 1, .3f, .3f),
		new MobType("slime", 5, 1, 1, .5f, 0.5f),
	};

	public Spawner() {
		random = new Random();
	}

	public void spawnMobs(GameState gameState) {
		// spawn mobs around the server
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Level) {
				spawnMobs((Level) obj, gameState);
			}
		}
	}

	public void spawnItems(GameState gameState) {
		// spawn mobs around the server
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Level) {
				spawnItems((Level) obj, gameState);
			}
		}
	}
	
	public void spawnDownstairs(GameState gameState) {
		List<Level> levels = new ArrayList<Level>(Constants.LEVEL_NUM);
		for (int i = 0; i < Constants.LEVEL_NUM - 1; i++)
			levels.add(null);
		
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Level && 
					!obj.get("number").equals(Constants.LEVEL_NUM)) {
				levels.set((int) ((Level) obj).get("number") - 1, (Level) obj);
			}
		}
		
		for (int i = 0; i < levels.size() - 1; i++)
			spawnDownstairsOnLevel((Level) levels.get(i),
					(Level) levels.get(i + 1));
	}
	
	public void spawnDownstairsOnLevel(Level level, Level nextLevel) {
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int levelNumber = (int) level.get("number");
		Log.info("Trying to spawn downstairs on level " + levelNumber);
		
		for (int i = 0; i < Constants.DOWNSTAIRS_PER_LEVEL; i++)
		{
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			
			if (!level.checkOccupied(y, x) &&
					!nextLevel.checkOccupied(y, x)) {
				Stairs downstairs = new Stairs("downstairs", 
						levelNumber + 1, x, y);
				Stairs upstairs = new Stairs("upstairs", 
						levelNumber, x, y);
				downstairs.put("level", levelNumber);
				upstairs.put("level", levelNumber + 1);
				Log.info("Game Logic", "Spawning downstair on level " 
						+ levelNumber);
				level.putChild(downstairs);
				nextLevel.putChild(upstairs);
			} else {
				// I'm sorry, but it's late and I don't care anymore
				i--;
			}
		}
	}

	public void spawnMobs(Level level, GameState gameState) {
		// let's try to keep at least one mob per chunk
		int targetMobs = (int) (level.getWidth() * level.getHeight() /
				Constants.CHUNKS_PER_MOB);
		int numMobs = 0;
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof Mob) {
				numMobs++;
			}
		}
		for (int i = numMobs; i < targetMobs; i++) {
			spawnOneMob(level, gameState);
		}
	}

	public void spawnItems(Level level, GameState gameState) {
		int targetItems = (int) (level.getWidth() * level.getHeight()
				/ Constants.CHUNKS_PER_ITEM);
		int numItems = 0;
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof Item) {
				numItems++;
			}
		}
		for (int i = numItems; i < targetItems; i++) {
			spawnOneItem(level, gameState);
		}
	}

	public boolean isNearPlayer(int x, int y, Level level, GameState gameState) {
		for (NetworkObject obj: gameState.getAllChildren().values()) {
			if (obj instanceof Player) {
				Player p = (Player) obj;
				if (p.getDungeonLevel() == (int) level.get("number")) {
					if (Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) < 64) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void spawnOneItem(Level level, GameState gameState) {
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		if (!level.checkOccupied(y, x) && !isNearPlayer(x, y, level, gameState)) {
			// spawn a mob there
			int itemType = random.nextInt(Constants.itemTypes.length);
			String name = Constants.itemTypes[itemType][0];
			String slot = Constants.itemTypes[itemType][1];
			Item item = new Item(name, slot, x, y);
			level.putChild(item);
		}
	}

	public void spawnOneMob(Level level, GameState gameState) {
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		if (!level.checkOccupied(y, x) && !isNearPlayer(x, y, level, gameState)) {
			// spawn a mob there
			MobType type = mobTypes[random.nextInt(mobTypes.length)];
			Mob mob = new Mob(type.name, x, y, type.maxHP, type.XP,
					type.damage, type.moveSpeed, type.attackSpeed);
			mob.put("level", level.get("number"));
			level.putChild(mob);
		}
	}
	
}
