package animated.spferical.netrogue.world;

import java.util.Random;

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
				spawnMobs((Level) obj);
			}
		}
	}

	public void spawnItems(GameState gameState) {
		// spawn mobs around the server
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Level) {
				spawnItems((Level) obj);
			}
		}
	}

	public void spawnMobs(Level level) {
		// let's try to keep at least one mob per chunk
		int targetMobs = level.getWidth() * level.getHeight();
		int numMobs = 0;
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof Mob) {
				numMobs++;
			}
		}
		for (int i = numMobs; i < targetMobs; i++) {
			spawnOneMob(level);
		}
	}

	public void spawnItems(Level level) {
		// one item per two chunks?
		// SOUNDS GREAT
		int targetItems = level.getWidth() * level.getHeight() / 2;
		int numItems = 0;
		for (NetworkObject obj : level.getAllChildren().values()) {
			if (obj instanceof Item) {
				numItems++;
			}
		}
		for (int i = numItems; i < targetItems; i++) {
			spawnOneItem(level);
		}
	}

	public void spawnOneItem(Level level) {
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		if (!level.checkOccupied(y, x)) {
			// spawn a mob there
			int itemType = random.nextInt(Constants.itemTypes.length);
			String name = Constants.itemTypes[itemType][0];
			String slot = Constants.itemTypes[itemType][1];
			Item item = new Item(name, slot, x, y);
			level.putChild(item);
		}
	}

	public void spawnOneMob(Level level) {
		int width = level.getWidth() * Constants.chunkSize;
		int height = level.getHeight() * Constants.chunkSize;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		if (!level.checkOccupied(y, x)) {
			// spawn a mob there
			MobType type = mobTypes[random.nextInt(mobTypes.length)];
			Mob mob = new Mob(type.name, x, y, type.maxHP, type.XP,
					type.damage, type.moveSpeed, type.attackSpeed);
			mob.put("level", level.get("number"));
			level.putChild(mob);
		}
	}
	
}
