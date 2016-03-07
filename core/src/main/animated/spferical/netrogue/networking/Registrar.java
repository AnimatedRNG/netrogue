package animated.spferical.netrogue.networking;

import java.util.ArrayList;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;

import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Tile;

public abstract class Registrar {

	public static void register(Kryo kryo) {
		kryo.register(TreeMap.class);
		kryo.register(ArrayList.class);
		kryo.register(Diff.class);
		kryo.register(AddAttributeDiff.class);
		kryo.register(AddChildDiff.class);
		kryo.register(DeleteAttributeDiff.class);
		kryo.register(DeleteChildDiff.class);
		kryo.register(ModifyAttributeDiff.class);
		kryo.register(ModifyChildDiff.class);
		kryo.register(NetworkObject.class);
		kryo.register(GameState.class);
		kryo.register(Tile.class);
		kryo.register(Chunk.class);
	}
}
