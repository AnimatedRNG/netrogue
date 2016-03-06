package animated.spferical.netrogue;

import animated.spferical.netrogue.networking.NetworkObject;

public class MapTile extends NetworkObject {

	private static final long serialVersionUID = 8152109643124254457L;

	enum Type {FLOOR, WALL};

	Type type;
}
