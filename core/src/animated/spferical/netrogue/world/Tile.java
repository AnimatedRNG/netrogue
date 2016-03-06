package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Tile extends NetworkObject {

	private static final long serialVersionUID = 8152109643124254457L;

	enum Type {FLOOR, WALL};

	Type type;
}
