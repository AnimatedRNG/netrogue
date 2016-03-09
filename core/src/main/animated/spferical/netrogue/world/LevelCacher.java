package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class LevelCacher {

	/** 2D array of chunk IDs.
	 * Indexed by [row][column]
	 */
	private long[][] chunkIDs;

	private long levelID;

	public LevelCacher(Level level) {
		this.levelID = level.ID;
		chunkIDs = new long[level.getWidth()][level.getHeight()];
	}

	public Chunk getChunk(GameState gameState, int row, int col) {
		if (row < 0 || col < 0 ||
				row >= chunkIDs.length || col >= chunkIDs[0].length) {
			// out of the map
			return null;
		}

		// get the level object
		NetworkObject obj = gameState.searchChildren(levelID);
		Level level = (Level) obj;

		if (chunkIDs[row][col] == 0) {
			Chunk chunk = level.getChunk(row, col);
			if (chunk != null) {
				chunkIDs[row][col] = chunk.ID;
			}
			return chunk;
		} else {
			return (Chunk) level.searchChildren(chunkIDs[row][col]);
		}
	}
}
