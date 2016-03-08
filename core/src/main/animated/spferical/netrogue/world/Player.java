package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Player extends NetworkObject {
	
	private static final long serialVersionUID = 818494368830828933L;

	public Player() {
		this(0, 0);
	}

	public Player(int x, int y) {
		super();
		setX(x);
		setY(y);
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
}
