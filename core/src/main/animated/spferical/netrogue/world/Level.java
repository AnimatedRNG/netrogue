package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Level extends NetworkObject {

	private static final long serialVersionUID = 1230412934127780197L;

	public Level() {
	}

	public Level(NetworkObject parent, int number) {
		super(parent);
		put("number", number);
	}
}
