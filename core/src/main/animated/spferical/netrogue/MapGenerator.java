package animated.spferical.netrogue;

import java.util.Arrays;
import java.util.Random;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.Tile;

public class MapGenerator {

	public static final int mapHeight = 62;
	public static final int mapWidth = 62;

	private static class MapVector {
		public int row, col;
		public MapVector(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public MapVector add(MapVector other) {
			return new MapVector(row + other.row, col + other.col);
		}

		public MapVector rotated90() {
			return new MapVector(col, -row);
		}

		public MapVector rotated270() {
			return new MapVector(-col, row);
		}

		public MapVector mul(int value) {
			return new MapVector(row * value, col * value);
		}
	}

	public static void digRect(Tile.Type[][] tiles, int row1, int col1, int row2, int col2) {
		for (int row = row1; row <= row2; row++) {
			for (int col = col1; col < col2; col++) {
				tiles[row][col] = Tile.Type.FLOOR;
			}
		}
	}

	public static boolean isWalls(Tile.Type[][] tiles, int row1, int col1, int row2, int col2) {
		for (int row = row1; row <= row2; row++) {
			for (int col = col1; col < col2; col++) {
				if (tiles[row][col] != Tile.Type.WALL) {
					return false;
				}
			}
		}
		return true;
	}

	public static MapVector isWallNextToFloor(Tile.Type[][] tiles,
			int row, int col) {
		// returns a mapvector if the tile at the passed location is a wall that is right next to a floor.
		// the mapvector is the direction from the floor to the wall.
		if (tiles[row][col] != Tile.Type.WALL) return null;
		if (tiles[row-1][col] == Tile.Type.FLOOR) return new MapVector(+1, 0);
		if (tiles[row+1][col] == Tile.Type.FLOOR) return new MapVector(-1, 0);
		if (tiles[row][col-1] == Tile.Type.FLOOR) return new MapVector(0, +1);
		if (tiles[row][col+1] == Tile.Type.FLOOR) return new MapVector(0, -1);
		return null;

	}

	public static void generateMap(NetworkObject level) {
		Log.info("Generating level" + level.ID + "...");
		// map dimensions, in chunks
		Random random = new Random();
		int chunkSize = Constants.chunkSize;

		Tile.Type[][] tiles = new Tile.Type
			[mapWidth * chunkSize][mapHeight * chunkSize];

		// initialize with walls
		for (int row = 0; row < tiles.length; row++) {
			Arrays.fill(tiles[row], Tile.Type.WALL);
		}

		// create first room, with up stairs, in the center
		digRect(tiles, tiles.length / 2 - 8, tiles[0].length / 2 - 8,
				tiles.length / 2 + 8, tiles[0].length / 2 + 8);

		// dig more
		for (int i = 0; i < 1000000; i++) {
			// find a wall
			int row = 0;
			int col = 0;
			MapVector pos = null;
			MapVector vec = null;
			while (vec == null) {
				row = 1 + random.nextInt(tiles.length - 2);
				col = 1 + random.nextInt(tiles[0].length - 2);
				pos = new MapVector(row, col);
				vec = isWallNextToFloor(tiles, row, col);
			}
			switch(random.nextInt(2)) {
				case 0:
					possiblyDigHallway(tiles, pos, vec, random);
					break;
				case 1:
					possiblyDigRoom(tiles, pos, vec, random);
					break;
			}
		}

		// create the 2d chunk array
		Chunk[][] chunks = new Chunk[mapWidth][mapHeight];

		for (int chunkRow = 0; chunkRow < chunks.length; chunkRow++) {
			for (int chunkCol = 0; chunkCol < chunks[0].length; chunkCol++) {
				// create the chunk
				chunks[chunkRow][chunkCol] = new Chunk(chunkRow, chunkCol);
				// get the tiles it starts with
				Tile.Type[][] chunkTiles = chunks[chunkRow][chunkCol].getTiles();

				// fill in each tile with what was generated above
				for (int tileRow = 0; tileRow < chunkSize; tileRow++) {
					for (int tileCol = 0; tileCol < chunkSize; tileCol++) {
						int row = chunkRow * chunkSize + tileRow;
						int col = chunkCol * chunkSize + tileCol;
						chunkTiles[tileRow][tileCol] = tiles[row][col];
					}
				}

				// add the chunk to the level
				level.putChild(chunks[chunkRow][chunkCol]);
			}
		}
		Log.info("Done generating level" + level.ID);
	}

	public static void possiblyDigHallway(Tile.Type[][] tiles, MapVector pos, MapVector vec, Random random) {
		// try to build a hallway off of the wall
		int hallwayLength = 1 + random.nextInt(50);
		// total hallway width = 1 + 2 * thickness
		int hallwayThickness = 1;
		MapVector corner1 = pos.add(vec.rotated90());
		MapVector corner2 = pos.add(vec.rotated270());
		MapVector corner3 = pos.add(vec.mul(hallwayLength).add(vec.rotated90().mul(hallwayThickness)));
		MapVector corner4 = pos.add(vec.mul(hallwayLength).add(vec.rotated270().mul(hallwayThickness)));
		int row1 = Math.min(corner1.row, Math.min(corner2.row, Math.min(corner3.row, corner4.row)));
		int row2 = Math.max(corner1.row, Math.max(corner2.row, Math.max(corner3.row, corner4.row)));
		int col1 = Math.min(corner1.col, Math.min(corner2.col, Math.min(corner3.col, corner4.col)));
		int col2 = Math.max(corner1.col, Math.max(corner2.col, Math.max(corner3.col, corner4.col)));
		if (row1 > 0 && row2 < tiles.length &&
				col1 > 0 && col2 < tiles.length &&
				isWalls(tiles, row1, col1, row2, col2)) {
			digRect(tiles, row1, col1, row2, col2);
		}
	}

	public static void possiblyDigRoom(Tile.Type[][] tiles, MapVector pos, MapVector vec, Random random) {
		// try to build a hallway off of the wall
		int roomLength = 10 + random.nextInt(10);
		// total room width = 1 + 2 * thickness
		int roomThickness = 5 + random.nextInt(5);
		MapVector corner1 = pos.add(vec.rotated90());
		MapVector corner2 = pos.add(vec.rotated270());
		MapVector corner3 = pos.add(vec.mul(roomLength).add(vec.rotated90().mul(roomThickness)));
		MapVector corner4 = pos.add(vec.mul(roomLength).add(vec.rotated270().mul(roomThickness)));
		int row1 = Math.min(corner1.row, Math.min(corner2.row, Math.min(corner3.row, corner4.row)));
		int row2 = Math.max(corner1.row, Math.max(corner2.row, Math.max(corner3.row, corner4.row)));
		int col1 = Math.min(corner1.col, Math.min(corner2.col, Math.min(corner3.col, corner4.col)));
		int col2 = Math.max(corner1.col, Math.max(corner2.col, Math.max(corner3.col, corner4.col)));
		if (row1 > 0 && row2 < tiles.length &&
				col1 > 0 && col2 < tiles.length &&
				isWalls(tiles, row1, col1, row2, col2)) {
			digRect(tiles, row1, col1, row2, col2);
		}
	}
}
