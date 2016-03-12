package animated.spferical.netrogue.world;

import java.util.Random;

import com.esotericsoftware.kryonet.Connection;

import animated.spferical.netrogue.Constants;

public class Player extends PositionedObject implements Actor {
	
	private static final long serialVersionUID = 818494368830828933L;

	public Player() {
		this(0, 0);
	}
	
	public Player(Connection connection, int x, int y) {
		this(x, y);
		this.put("connection", connection.getID());
		// Temporary name until player identifies themselves
		this.put("name", connection.getID());
	}

	public Player(int x, int y) {
		super("player", x, y);
		setX(x);
		setY(y);
		
		this.put("characterLevel", 1);
		this.put("hp", calculateMaxHP((Integer) this.get("characterLevel")));
		
		this.put("ap", calculateMaxAP((Integer) this.get("characterLevel")));
		this.put("ap_accumulator", 0.0f);
		this.put("xp", 0);
		
		this.put("level", 1);
		this.put("timeSinceLastAction", (Float) 0.0f);
	}

	public int getDungeonLevel() {
		return (int) get("level");
	}

	public int calculateDamage() {
		return (int) this.get("characterLevel");
	}

	public void takeDamage(int damage) {
		int hp = ((int) get("hp")) - damage;
		if (hp <= 0) {
			put("dead", true);
		}
		put("hp", hp);
	}

	public int getConnectionID() {
		return (int) get("connection");
	}
	
	public void randomlyAssignName() {
		this.put("name", Constants.names[new Random().nextInt(
				Constants.names.length)]);
	}
	
	public int calculateMaxHP(int characterLevelNumber) {
		return Constants.BASE_HP + 
				characterLevelNumber * Constants.EXTRA_HP_PER_LEVEL;
	}
	
	public int calculateMaxAP(int characterLevelNumber) {
		return Constants.BASE_AP + 
				characterLevelNumber * Constants.EXTRA_AP_PER_LEVEL;
	}
	
	public void onKillMob(Mob mob) {
		boolean leveledUp = this.gainExperience((int) mob.get("xp"));
		if (!leveledUp)
			this.addPlayerMessage("You killed a " + 
					mob.get("type") + " for "
					+ mob.get("xp") + " XP.");
	}
	
	public boolean gainExperience(int xp) {
		int currentXP = (int) this.get("xp") + xp;
		int currentLevel = (int) this.get("characterLevel");
		int newLevel = appropriateLevelUp(currentXP);
		
		this.put("xp", currentXP);
		if (newLevel > currentLevel)
		{
			this.put("characterLevel", newLevel);
			this.put("xp", 0);
			this.put("hp", this.calculateMaxHP(newLevel));
			this.put("ap", this.calculateMaxAP(newLevel));
			this.addPlayerMessage("Welcome to Level " + newLevel);
			return true;
		}
		return false;
	}
	
	public int appropriateLevelUp(int currentXP) {
		float xp = currentXP;
		if (xp <= 0)
			xp = 1;
		return (int) (Math.log((float) currentXP / (float) Constants.XP_LEVEL_MODIFIER)
				/ Math.log(2)) + 1;
	}

	public void addPlayerMessage(String message) {
		this.put("playerMessage", message);
		this.put("playerMessageID", new Random().nextLong());
	}
	
	@Override
	public void onUpdate(GameState gameState, float dt) {
		float currentAPAccumulator = (Float) this.get("ap_accumulator");
		
		if (currentAPAccumulator > Constants.AP_REGEN_TIME)
		{
			int max_ap = this.calculateMaxAP((int) this.get("characterLevel"));
			int current_ap = (int) this.get("ap");
			if (current_ap < max_ap)
				current_ap += Constants.AP_REGEN_AMOUNT;
			if (current_ap > max_ap)
				current_ap = max_ap;
			this.put("ap", current_ap);
			currentAPAccumulator = 0;
		}
		
		this.put("ap_accumulator", currentAPAccumulator + dt);
	}

	@Override
	public void onDeath(GameState gameState) {
		// TODO: add a tombstone
	}
}
