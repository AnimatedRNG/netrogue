package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Chunk extends NetworkObject {

	private static final long serialVersionUID = -7476640053641021374L;

	Tile.Type[][] tiles = new Tile.Type[16][16];
}
