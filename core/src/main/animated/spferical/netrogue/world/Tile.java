package animated.spferical.netrogue.world;

import java.util.Arrays;

import animated.spferical.netrogue.networking.NetworkObject;

public class Tile extends NetworkObject {

	private static final long serialVersionUID = 8152109643124254457L;

	public enum Type {FLOOR, WALL};

	public Tile() {
		put("type", Type.WALL);
	}

	public Type getType() {
		return (Type) get("type");
	}
}


