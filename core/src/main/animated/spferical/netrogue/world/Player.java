package animated.spferical.netrogue.world;

import java.util.Random;

import com.esotericsoftware.kryonet.Connection;

import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.networking.NetworkObject;

public class Player extends NetworkObject {
	
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
}
