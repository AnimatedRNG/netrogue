package animated.spferical.netrogue;

public class Constants {
	public static final float actionDelay = 0.1f; // in seconds
	public static final int tileSize = 64;
	public static final int chunkSize = 16;
	
	public static final int BASE_HP = 10;
	public static final int EXTRA_HP_PER_LEVEL = 1;
	
	public static final int BASE_AP = 5;
	public static final int EXTRA_AP_PER_LEVEL = 1;
	
	public static final float AP_REGEN_TIME = 0.25f;
	public static final float AP_REGEN_AMOUNT = 1;
	
	// New Level = log2(XP / 20) + 1
	public static final int XP_LEVEL_MODIFIER = 1;
	public static final int XP_BASE_NUMBER = 2;
	
	public static final int MELEE_BUFF = 1;
	
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
}
