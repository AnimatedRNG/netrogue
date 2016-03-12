package animated.spferical.netrogue.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.Constants.SpellInfo;
import animated.spferical.netrogue.networking.LocalCache;
import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.networking.StringArray;

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
		
		this.put("melee_buff", 0);
		this.put("spell_buff", 0);
		
		this.put("level", 1);
		this.put("timeSinceLastAction", (Float) 0.0f);

		this.put("selection", 0);
		
		LocalCache.put("player_random", new Random(this.ID));
	}

	public int getDungeonLevel() {
		return (int) get("level");
	}

	public int calculateMeleeDamage() {
		int characterLevel = (int) this.get("characterLevel");
		int meleeBuff = (int) this.get("melee_buff");
		int attack = characterLevel + meleeBuff * Constants.MELEE_BUFF;
		String weapon = (String) this.get("weapon");
		if (weapon != null) {
			int weaponDamage = Constants.weaponInfos.get(weapon).damage;
			attack += weaponDamage;
		}
		return attack;
	}
	
	public int calculateSpellDamage(SpellInfo spell) {
		int baseDamage = (int) spell.damage;
		int spellBuff = (int) this.get("spell_buff");
		int attack = baseDamage + spellBuff * Constants.SPELL_BUFF;
		return attack;
	}

	public void takeDamage(int damage, String attacker) {
		int hp = ((int) get("hp")) - damage;
		if (hp <= 0) {
			put("dead", true);
			put("killer", attacker);
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
	
	public float calculateSpellRange(SpellInfo spell) {
		int spellBuff = (int) this.get("spell_buff");
		return spell.range + spellBuff * Constants.SPELL_RANGE_BUFF;
	}
	
	public int calculateSpellAPDrain(SpellInfo spell) {
		int spellBuff = (int) this.get("spell_buff");
		if (spellBuff > Constants.SPELL_SAVINGS_CUTOFF)
			return spell.apCost / Constants.SPELL_SAVINGS_AMOUNT;
		else
			return spell.apCost;
	}
	
	public float calculateSpellDuration(SpellInfo spell) {
		int spellBuff = (int) this.get("spell_buff");
		return Constants.SPELL_BASE_DURATION + spellBuff * Constants.SPELL_DURATION_BUFF;
	}
	
	public float calculateSpellSpeed(SpellInfo spell) {
		int spellBuff = (int) this.get("spell_buff");
		float speed = Constants.SPELL_BASE_SPEED - spellBuff * Constants.SPELL_SPEED_BUFF;
		if (speed < Constants.FASTEST_SPELL)
			return Constants.FASTEST_SPELL;
		else
			return speed;
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
			this.addPlayerMessage("Welcome to Level " + newLevel + 
					".\n How do you want to invest your skill points?");
			
			HashMap<String, Runnable> options = new HashMap<>();
			options.put("Melee Attacks", new Runnable() {
				@Override
				public void run() {
					Log.info("Game Logic", "Player " + this + 
							" is investing in melee attacks");
					int meleeBuff = (int) get("melee_buff");
					put("melee_buff", meleeBuff + 1);
				}
			});
			options.put("Offensive Spellcasting", new Runnable() {
				@Override
				public void run() {
					Log.info("Game Logic", "Player " + this + 
							" is investing in offensive spellcasting");
					int spellBuff = (int) get("spell_buff");
					put("spell_buff", spellBuff + 1);
				}
			});
			this.addPlayerGUI(null, options);
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
		this.put("playerMessageID", ((Random) LocalCache.get("player_random")).
				nextLong());
	}
	
	public void addPlayerGUI(String message, Map<String, Runnable> options) {
		if (message != null)
			this.addPlayerMessage(message);
		this.put("playerGUI_options", new StringArray(options.keySet()));
		LocalCache.put("playerGUI_callbacks", 
				new ArrayList<Runnable>(options.values()));
		this.put("playerGUI_ID", ((Random) LocalCache.get("player_random")).
				nextLong());
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
		
		List<NetworkObject> stairs = gameState.getLevelByNumber(
				this.getDungeonLevel()).getAllChildrenOfType(
						Stairs.class, false);
		
		boolean ignore = false;
		for (NetworkObject stair : stairs) {
			if (stair.get("x").equals(get("x")) &&
					stair.get("y").equals(get("y")) &&
					stair.get("level").equals(get("level")))
			{
				if (!this.check("justOnStairs"))
				{
					this.put("level", stair.get("targetLevel"));
					this.put("justOnStairs", true);
					if (stair.get("type").equals("downstairs"))
						this.addPlayerMessage("Descended to dungeon level "
								+ stair.get("targetLevel"));
					else
						this.addPlayerMessage("Ascended to dungeon level "
								+ stair.get("targetLevel"));
				}
				ignore = true;
			}
		}
		if (!ignore)
			this.remove("justOnStairs");
	}
	
	public void castSpell(GameState gameState, int theta, Constants.SpellInfo spell) {
		Projectile projectile = new Projectile(spell.name, this.getX(), this.getY(),
				theta, this.calculateSpellSpeed(spell), this.calculateSpellDuration(spell),
				this.calculateSpellDamage(spell), this.ID);
		projectile.put("level", this.get("level"));
		int ap = ((int) get("ap")) - calculateSpellAPDrain(spell);
		if (ap > 0)
			this.put("ap", ap);
		else
			this.put("ap", 0);
		Log.info("Game Logic", "Firing spell");
		gameState.getLevelByNumber((int) this.get("level")).putChild(projectile);
	}

	@Override
	public void onDeath(GameState gameState) {
		String message = (String) get("name") + " was killed at level " + get("level");
		String killer = (String) get("killer");
		if (killer != null) {
			message += " by a " + killer;
		}
		message += " on dungeon level " + getDungeonLevel();
		gameState.announce(message);
		PositionedObject tombStone = new Tombstone(getX(), getY(), message);
		NetworkObject level = gameState.getLevelByNumber(getDungeonLevel());
		level.putChild(tombStone);
	}
	
	// Handles extra stuff that the player sends us
	// One day we'll move all that crap in handlePlayerInput here
	public void onPlayerInput(ClientInputState inputState) {
		if (inputState.inputType == ClientInputState.InputType.SELECT_OPTION)
			Log.info("Player selected option " + inputState.intInput);
		@SuppressWarnings("unchecked")
		List<Runnable> callbacks = 
				(List<Runnable>) LocalCache.get("playerGUI_callbacks");
		if (callbacks != null && 
				inputState.inputType == ClientInputState.InputType.SELECT_OPTION
				&& callbacks.size() > inputState.intInput 
				&& inputState.intInput >= 0)
		{
			try {
				callbacks.get(inputState.intInput).run();
				LocalCache.put("playerGUI_callbacks", null);
			} catch (Exception e) {
				Log.error("Game Logic", "Unable to handle Player GUI input", e);
			}
		}

		if (inputState.inputType == ClientInputState.InputType.SELECT_ITEM) {
			put("selection", inputState.intInput);
		}
	}
}
