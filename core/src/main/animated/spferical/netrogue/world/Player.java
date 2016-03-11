package animated.spferical.netrogue.world;

import java.util.Random;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.networking.NetworkObject;

public class Player extends NetworkObject implements Actor {
	
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
		super();
		setX(x);
		setY(y);
		
		this.put("characterLevel", 1);
		this.put("hp", calculateMaxHP((Integer) this.get("characterLevel")));
		
		this.put("ap", calculateMaxAP((Integer) this.get("characterLevel")));
		this.put("ap_accumulator", 0.0f);
		
		this.put("level", 1);
		this.put("timeSinceLastAction", (Float) 0.0f);
	}

	public int getDungeonLevel() {
		return (int) get("level");
	}

	public int getX() {
		return (int) get("x");
	}

	public int getY() {
		return (int) get("y");
	}

	public void setX(int x) {
		put("x", x);
	}

	public void setY(int y) {
		put("y", y);
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

	@Override
	public void onUpdate(GameState gameState, float dt) {
		float currentAPAccumulator = (Float) this.get("ap_accumulator");
		
		if (currentAPAccumulator > Constants.AP_REGEN_TIME)
		{
			float max_ap = (float) this.calculateMaxAP((int) this.get("characterLevel"));
			int current_ap = (int) this.get("ap");
			if (current_ap < max_ap)
				current_ap += Constants.AP_REGEN_AMOUNT;
			this.put("ap", current_ap);
			currentAPAccumulator = 0;
		}
		
		this.put("ap_accumulator", currentAPAccumulator + dt);
	}
}
