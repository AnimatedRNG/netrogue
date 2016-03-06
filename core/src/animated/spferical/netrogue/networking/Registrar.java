package animated.spferical.netrogue.networking;

import java.util.ArrayList;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;

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
		kryo.register(Connection.class);
		kryo.register(Connection[].class);
		kryo.register(GameState.class);
		kryo.register(Server.class);
		kryo.register(Client.class);
		kryo.register(NetworkObject.class);
		kryo.register(Player.class);
		kryo.register(InfoQuery.class);
		kryo.register(InfoResponse.class);
	}
}
