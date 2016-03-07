package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Chunk extends NetworkObject {

	private static final long serialVersionUID = -7476640053641021374L;

	public Chunk() {
	}

	public Chunk(NetworkObject parent, int chunkRow, int chunkCol) {
		super(parent);
		Tile.Type[][] tiles = new Tile.Type[16][16];
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[0].length; col++) {
				tiles[row][col] = Tile.Type.WALL;
			}
		}
		put("tiles", tiles);
		put("row", chunkRow);
		put("col", chunkCol);
	}

	public int getRow() {
		return (int) get("row");
	}
	public int getCol() {
		return (int) get("col");
	}

	public Tile.Type[][] getTiles() {
		return (Tile.Type[][]) get("tiles");
	}
}
