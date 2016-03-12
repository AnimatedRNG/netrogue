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
		public int level;
		public String name;
		public int maxHP;
		public int damage;
		public int XP;
		public float moveSpeed;
		public float attackSpeed;

		public MobType(int level, String name, int maxHP, int XP, int damage,
				float moveSpeed, float attackSpeed) {
			this.level = level;
			this.name = name;
			this.maxHP = maxHP;
			this.damage = damage;
			this.moveSpeed = moveSpeed;
			this.attackSpeed = attackSpeed;
			this.XP = XP;
		}
	};

	class ItemType {
		public int level;
		public String item;
		public String slot;
		public ItemType(int level, String item, String slot) {
			this.level = level;
			this.item = item;
			this.slot = slot;
		}
	}

	final MobType[] mobTypes = {
		// new MobType("name", HP, XP, damage, moveSpeed, attackSpeed);
		new MobType(1, "worm", 20, 1, 1, 1, 1),
		new MobType(1, "ant", 14, 2, 1, .4f, .4f),
		new MobType(1, "beetle", 12, 1, 1, .75f, .5f),
		new MobType(1, "butterfly", 10, 1, 1, .3f, .3f),
		new MobType(1, "slime", 10, 1, 1, .5f, 0.5f),
		new MobType(2, "bat", 20, 2, 2, .3f, 0.5f),
		new MobType(2, "big worm", 40, 2, 3, 1, 0.9f),
		new MobType(3, "big bat", 40, 2, 2, .3f, .5f),
		new MobType(3, "scorpion", 20, 2, 5, .3f, .5f),
		new MobType(3, "mimic", 20, 2, 5, 999999f, .5f),

		// keep boss at bottom of list
		new MobType(4, "boss", 100, 0, 10, .5f, .5f),
	};

	final ItemType[] itemTypes = {
		new ItemType(1, "dagger", "weapon"),
		new ItemType(1, "healing potion", "potion"),
		new ItemType(1, "fire", "spell"),
		new ItemType(1, "ice", "spell1"),
		new ItemType(1, "lightning", "spell2"),
		new ItemType(1, "club", "weapon"),
		new ItemType(2, "mace", "weapon"),
		new ItemType(2, "sword", "weapon"),
		new ItemType(2, "healing potion", "potion"),
		new ItemType(2, "fire", "spell"),
		new ItemType(2, "ice", "spell1"),
		new ItemType(2, "lightning", "spell2"),
		new ItemType(3, "axe", "weapon"),
		new ItemType(3, "double-axe", "weapon"),
		new ItemType(3, "healing potion", "potion"),
		new ItemType(3, "fire", "spell"),
		new ItemType(3, "ice", "spell1"),
		new ItemType(3, "lightning", "spell2"),
		new ItemType(4, "healing potion", "potion"),
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
		// create and populate list of levels
		List<Level> levels = new ArrayList<Level>(Constants.LEVEL_NUM);
		for (int i = 0; i < Constants.LEVEL_NUM; i++)
			levels.add(null);
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Level) {
				levels.set((int) ((Level) obj).get("number") - 1, (Level) obj);
			}
		}
		
		// spawn down stairs on each level from above
		for (int i = 0; i < levels.size() - 1; i++)
			spawnDownstairsOnLevel((Level) levels.get(i),
					(Level) levels.get(i + 1));
	}
	
	public void spawnDownstairsOnLevel(Level level, Level nextLevel) {
		int width = nextLevel.getWidth() * Constants.chunkSize;
		int height = nextLevel.getHeight() * Constants.chunkSize;
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
		if ((int)level.get("number") == 4) {
			// spawn only one mob, the big boss
			boolean playerOnLevel = false;
			for (NetworkObject obj : gameState.getAllChildrenOfType(Player.class, false)) {
				if (((Player) obj).getDungeonLevel() == 4) {
					playerOnLevel = true;
					break;
				}
			}
			if (!playerOnLevel) {
				int x = level.getWidth() * Constants.chunkSize / 2;
				int y = level.getHeight() * Constants.chunkSize / 2;
				while (!level.checkOccupied(y, x)) {
					y++;
				}
				y--;

				// reset boss to starting location
				boolean bossGone = true;
				for (NetworkObject obj: level.getAllChildrenOfType(Mob.class, false)) {
					if ((String) obj.get("type") == "boss") {
						bossGone = false;
						Mob mob = (Mob) obj;
						if (mob.getX() != x || mob.getY() != y) {
							level.removeChild(obj.ID);
							bossGone = true;
						}
					}
				}
				if (bossGone) {
					MobType bossType = mobTypes[mobTypes.length - 1];
					Mob mob = new Mob(bossType.name, x, y, bossType.maxHP, bossType.XP,
							bossType.damage, bossType.moveSpeed, bossType.attackSpeed);
					mob.put("level", level.get("number"));
					level.putChild(mob);
				}
			}
		} else {
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
	}

	public void spawnItems(Level level, GameState gameState) {
		if ((int) level.get("number") == 4) {
			// don't spawn items on final level
			return;
		}
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
		if (Math.pow(x, 2) + Math.pow(y, 2) < 64) {
			return true;
		}
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
			ItemType type = getRandomItem((int) level.get("number"));
			if (type.item == "healing potion") {
				level.putChild(new HealingPotion(x, y));
			} else if (type != null) {
				String name = type.item;
				String slot = type.slot;
				Item item = new Item(name, slot, x, y);
				level.putChild(item);
			}
		}
	}

	public ItemType getRandomItem(int level) {
		int totalItems = 0;
		for (ItemType t : itemTypes) {
			if (t.level == level) {
				totalItems++;
			}
		}
		int choice = random.nextInt(totalItems);
		int index = 0;
		for (ItemType t : itemTypes) {
			if (t.level == level) {
				if (choice == index) {
					return t;
				}
				index += 1;
			}
		}
		return null;
	}

	public MobType getRandomMob(int level) {
		int totalMobs = 0;
		for (MobType t : mobTypes) {
			if (t.level == level) {
				totalMobs++;
			}
		}
		int choice = random.nextInt(totalMobs);
		int index = 0;
		for (MobType t : mobTypes) {
			if (t.level == level) {
				if (choice == index) {
					return t;
				}
				index += 1;
			}
		}
		return null;
	}

	public void spawnOneMob(Level level, GameState gameState) {
		int levelNumber = (int) level.get("number");
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		if (!level.checkOccupied(y, x) && !isNearPlayer(x, y, level, gameState)) {
			// spawn a mob there
			MobType type = getRandomMob(levelNumber);
			Mob mob = new Mob(type.name, x, y, type.maxHP, type.XP,
					type.damage, type.moveSpeed, type.attackSpeed);
			mob.put("level", level.get("number"));
			level.putChild(mob);
		}
	}
	
}
