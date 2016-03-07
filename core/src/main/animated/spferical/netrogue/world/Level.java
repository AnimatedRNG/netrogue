package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Level extends NetworkObject {
	public Level() {
	}

	public Level(NetworkObject parent, int number) {
		super(parent);
		put("number", number);
	}
}
