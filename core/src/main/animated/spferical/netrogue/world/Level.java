package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Level extends NetworkObject {

	private static final long serialVersionUID = 1230412934127780197L;

	/** 2D array of chunk IDs.
	 * Indexed by [row][column]
	 */
	private long[][] chunkIDs;

	public Level() {
	}

	/**
	 *
	 *
	 * @param parent
	 * @param number
	 * @param width width of the level, in chunks
	 * @param height height of the level, in chunks
	 */
	public Level(NetworkObject parent, int number, int width, int height) {
		super(parent);
		put("number", number);
		chunkIDs = new long[width][height];
	}

	public Chunk getChunk(int row, int col) {
		if (row < 0 || col < 0 ||
				row >= chunkIDs.length || col >= chunkIDs[0].length)
			// out of the map
			return null;
		if (chunkIDs[row][col] == 0) {
			for (NetworkObject child : getAllChildren().values()) {
				if (!(child instanceof Chunk)) continue;
				Chunk chunk = (Chunk) child;
				if (chunk.getRow() == row && chunk.getCol() == col) {
					chunkIDs[row][col] = chunk.ID;
					break;
				}
			}
		}
		return (Chunk) getChild(chunkIDs[row][col]);
	}
}
