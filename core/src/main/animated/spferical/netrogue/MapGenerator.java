package animated.spferical.netrogue;

import java.util.Random;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.Tile;

public class MapGenerator {
	public void digRect(Tile.Type[][] tiles, int row1, int col1, int row2, int col2) {
		for (int row = row1; row <= row2; row++) {
			for (int col = col1; col < col2; col++) {
				tiles[row][col] = Tile.Type.FLOOR;
			}
		}
	}

	public void generateMap(NetworkObject level) {
		// map dimensions, in chunks
		int mapHeight = 62;
		int mapWidth = 62;
		Random random = new Random();

		Tile.Type[][] tiles = new Tile.Type[mapWidth * 16][mapHeight * 16];

		// create first room, with up stairs, in the center
		digRect(tiles, tiles.length / 2 - 8, tiles[0].length / 2 - 8,
				tiles.length / 2 + 8, tiles[0].length / 2 + 8);

		// dig more

		// initialize a giant 2d array of chunks
		Chunk[][] chunks = new Chunk[mapWidth][mapHeight];
		for (int row = 0; row < chunks.length; row++) {
			for (int col = 0; col < chunks[0].length; col++) {
				chunks[row][col] = new Chunk(level, row, col);
				level.putChild(chunks[row][col]);
			}
		}

		// TODO: set each tile in the chunks
	}
}
