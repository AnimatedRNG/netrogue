package animated.spferical.netrogue;

import animated.spferical.netrogue.networking.NetworkObject;

public class MapChunk extends NetworkObject {

	private static final long serialVersionUID = -7476640053641021374L;

	MapTile.Type[][] tiles = new MapTile.Type[16][16];
}
