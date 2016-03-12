package animated.spferical.netrogue;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static final float actionDelay = 0.25f; // in seconds
	public static final int tileSize = 64;
	public static final int chunkSize = 16;
	
	public static final int BASE_HP = 10;
	public static final int EXTRA_HP_PER_LEVEL = 1;
	
	public static final int BASE_AP = 5;
	public static final int EXTRA_AP_PER_LEVEL = 1;
	
	public static final float AP_REGEN_TIME = 1f/3;
	public static final float AP_REGEN_AMOUNT = 1;
	
	public static final int ARROW_PROXIMITY = 12;
	public static final float ARROW_DISTANCE = 3f;
	
	// New Level = log2(XP / 20) + 1
	public static final int XP_LEVEL_MODIFIER = 1;
	public static final int XP_BASE_NUMBER = 2;
	
	public static final int MELEE_BUFF = 1;
	public static final int SPELL_BUFF = 1;
	public static final int SPELL_RANGE_BUFF = 1;
	public static final int SPELL_SAVINGS_CUTOFF = 3;
	public static final int SPELL_SAVINGS_AMOUNT = 2;
	public static final float SPELL_BASE_DURATION = 1;
	public static final float SPELL_DURATION_BUFF = 1;
	public static final float SPELL_BASE_SPEED = 0.2f;
	public static final float SPELL_SPEED_BUFF = 0.02f;
	public static final float FASTEST_SPELL = 0.05f;
	
	public static final int LEVEL_NUM = 4;
	
	public static final String[] names = new String[] {
		"Aeto",
		"Dasre",
		"Der",
		"Dera",
		"Ditani",
		"Frig",
		"Gabram",
		"Gal",
		"Govul",
		"Hhart",
		"Ieni",
		"Marih",
		"Mribm",
		"Nalah",
		"Rahi",
		"Ranih",
		"Relah",
		"Riter",
		"Ror",
		"Side"
	};

	public static final int FLOOR_DENSITY = 1000;
	
	public static final float CHUNKS_PER_ITEM = 3;
	public static final float CHUNKS_PER_MOB = 1;
	public static final int DOWNSTAIRS_PER_LEVEL = 4;

	public static final String[] slots = {"weapon", "spell", "spell1", "spell2"};

	public static class SpellInfo {
		public float range;
		public int damage;
		public int apCost;
		public String name;
		public SpellInfo(float range, int damage, int apCost, String name) {
			this.range = range;
			this.damage = damage;
			this.apCost = apCost;
			this.name = name;
		}

		public String toString() {
			return "Range: " + range + " Damage: " + damage + " AP Cost: " + apCost;
		}
	};

	public static class WeaponInfo {
		public int damage;
		public WeaponInfo(int damage) {
			this.damage = damage;
		}

		public String toString() {
			return "Damage: " + damage;
		}
	}

	public static final Map<String, SpellInfo> spellInfos = new HashMap<>();
	public static final Map<String, WeaponInfo> weaponInfos = new HashMap<>();

	static {
		spellInfos.put("fire",
			new SpellInfo(5.0f, 3, 3, "fire"));
		spellInfos.put("ice",
			new SpellInfo(10.0f, 2, 4, "ice"));
		spellInfos.put("lightning",
			new SpellInfo(15.0f, 1, 3, "lightning"));
		weaponInfos.put("dagger", new WeaponInfo(1));
		weaponInfos.put("club", new WeaponInfo(2));
		weaponInfos.put("mace", new WeaponInfo(4));
		weaponInfos.put("sword", new WeaponInfo(6));
		weaponInfos.put("axe", new WeaponInfo(8));
		weaponInfos.put("double-axe", new WeaponInfo(10));
	}
}
