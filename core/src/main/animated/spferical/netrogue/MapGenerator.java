package animated.spferical.netrogue;

import java.util.Arrays;
import java.util.Random;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.Tile;

public class MapGenerator {

	public static final int mapHeight = 62;
	public static final int mapWidth = 62;
	public static void digRect(Tile.Type[][] tiles, int row1, int col1, int row2, int col2) {
		for (int row = row1; row <= row2; row++) {
			for (int col = col1; col < col2; col++) {
				tiles[row][col] = Tile.Type.FLOOR;
			}
		}
	}

	public static void generateMap(NetworkObject level) {
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
	}
}
