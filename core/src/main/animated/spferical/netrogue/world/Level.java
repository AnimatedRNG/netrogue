package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Level extends NetworkObject {

	private static final long serialVersionUID = 1230412934127780197L;

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
	public Level(int number, int width, int height) {
		super();
		put("number", number);
		put("width", width);
		put("height", height);
	}

	public int getWidth() {
		return (int) get("width");
	}

	public int getHeight() {
		return (int) get("height");
	}

	public int getNumber() {
		return (int) get("number");
	}

	public Chunk getChunk(int row, int col) {
		for (NetworkObject child : getAllChildren().values()) {
			if (!(child instanceof Chunk)) continue;
			Chunk chunk = (Chunk) child;
			if (chunk.getRow() == row && chunk.getCol() == col) {
				return chunk;
			}
		}
		return null;
	}
}
