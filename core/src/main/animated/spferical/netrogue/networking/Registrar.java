package animated.spferical.netrogue.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.Tile;
import animated.spferical.netrogue.world.TileTypeArray;

public abstract class Registrar {

	public static void register(Kryo kryo) {
		kryo.register(TreeMap.class);
		kryo.register(ArrayList.class);
		kryo.register(Diff.class);
		kryo.register(List.class);
		kryo.register(AddAttributeDiff.class);
		kryo.register(AddChildDiff.class);
		kryo.register(DeleteAttributeDiff.class);
		kryo.register(DeleteChildDiff.class);
		kryo.register(ModifyAttributeDiff.class);
		kryo.register(ModifyChildDiff.class);
		kryo.register(Connection.class);
		kryo.register(Connection[].class);
		kryo.register(GameState.class);
		kryo.register(Server.class);
		kryo.register(Client.class);
		kryo.register(NetworkObject.class);
		kryo.register(Player.class);
		kryo.register(InfoQuery.class);
		kryo.register(InfoResponse.class);
		kryo.register(GameState.class);
		kryo.register(Tile.class);
		kryo.register(Chunk.class);
		kryo.register(Level.class);
		kryo.register(ClientInputState.class);
		kryo.register(Tile.Type.class);
		kryo.register(Tile.Type[].class);
		kryo.register(Tile.Type[][].class);
		kryo.register(TileTypeArray.class);
	}
}
