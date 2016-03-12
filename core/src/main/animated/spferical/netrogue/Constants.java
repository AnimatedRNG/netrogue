package animated.spferical.netrogue;

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
	
	// New Level = log2(XP / 20) + 1
	public static final int XP_LEVEL_MODIFIER = 1;
	public static final int XP_BASE_NUMBER = 2;
	
	public static final int MELEE_BUFF = 1;
	
	public static final int LEVEL_NUM = 3;
	
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
	
	public static final float CHUNKS_PER_ITEM = 10;
	public static final float CHUNKS_PER_MOB = 1;
	public static final int DOWNSTAIRS_PER_LEVEL = 3;

	public static final String[] slots = {"weapon", "spell", "bleh", "blah"};

	public static final String[][] itemTypes = {{
		// item name, slot
		"fire", "spell"
	}};
}
