package animated.spferical.netrogue.world;

import animated.spferical.netrogue.Constants;
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
	
	/**
	 * Returns whether you can move into a given tile
	 * 
	 * @param row
	 * @param column
	 * @return whether you can move into the tile
	 */
	public boolean checkOccupied(int row, int column) {
		Chunk chunk = this.getChunk(row / Constants.chunkSize,
				column / Constants.chunkSize);
		return chunk.isOccupied(row % Constants.chunkSize, 
				column % Constants.chunkSize) 
				&& checkMobCollision(row, column) == null; 
	}

	public Mob checkMobCollision(int row, int column) {
		for (NetworkObject obj : getAllChildren().values()) {
			if (obj instanceof Mob) {
				Mob m = (Mob) obj;
				if (m.getX() == column && m.getY() == row) {
					return m;
				}
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(" "+this.getClass().getName() +" " + this.ID + "lastUpdate:" + this.lastUpdate);
		int numChunks = 0;
		int numMobs = 0;
		for (NetworkObject obj : getAllChildren().values()) {
			if (obj instanceof Mob) {
				numMobs++;
			} else if (obj instanceof Chunk) {
				numChunks++;
			}
		}
		builder.append("\n  " + numChunks + " Chunks\n  " + numMobs + " Mobs");
		return builder.toString();
	}
}
