package animated.spferical.netrogue.world;

import java.io.Serializable;
import java.util.Arrays;

public class TileTypeArray implements Serializable {
		
	public Tile.Type[][] tiles;
	
	public TileTypeArray() {
		this(0, 0);
	}
	
	public TileTypeArray(int dim1, int dim2) {
		this.tiles = new Tile.Type[dim1][dim2];
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof TileTypeArray)
			return Arrays.deepEquals(tiles, ((TileTypeArray) other).tiles);
		else
			return false;
	}
}
