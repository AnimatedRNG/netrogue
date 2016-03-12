package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class PositionedObject extends NetworkObject {

	private static final long serialVersionUID = -7541965865160825690L;

	public PositionedObject() {
	}

	public PositionedObject(String type, int x, int y) {
		put("type", type);
		put("x", x);
		put("y", y);
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
