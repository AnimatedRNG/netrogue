package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Chunk extends NetworkObject {

	private static final long serialVersionUID = -7476640053641021374L;

	public Chunk() {
	}

	public Chunk(int chunkRow, int chunkCol) {
		super();
		TileTypeArray tiles = new TileTypeArray(16, 16);
		for (int row = 0; row < tiles.tiles.length; row++) {
			for (int col = 0; col < tiles.tiles[0].length; col++) {
				tiles.tiles[row][col] = Tile.Type.WALL;
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

	public TileTypeArray getTiles() {
		return (TileTypeArray) get("tiles");
	}
	
	/**
	 * Returns whether you can move into a given tile
	 * 
	 * @param row
	 * @param column
	 * @return whether you can move there
	 */
	public boolean isOccupied(int row, int column) {
		return this.getTiles().tiles[row][column] == Tile.Type.WALL;
	}
}
