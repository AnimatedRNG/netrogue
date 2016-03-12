package animated.spferical.netrogue.networking;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import animated.spferical.netrogue.ChatLine;
import animated.spferical.netrogue.ChatNetworkObject;
import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.world.Chunk;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.HealingPotion;
import animated.spferical.netrogue.world.Item;
import animated.spferical.netrogue.world.Level;
import animated.spferical.netrogue.world.Mob;
import animated.spferical.netrogue.world.Player;
import animated.spferical.netrogue.world.PositionedObject;
import animated.spferical.netrogue.world.Projectile;
import animated.spferical.netrogue.world.Stairs;
import animated.spferical.netrogue.world.Tile;
import animated.spferical.netrogue.world.TileTypeArray;
import animated.spferical.netrogue.world.Tombstone;

public abstract class Registrar {

	public static void register(Kryo kryo) {
		kryo.register(TreeMap.class);
		kryo.register(ArrayList.class);
		kryo.register(Diff.class);
		kryo.register(List.class);
		kryo.register(String[].class);
		kryo.register(StringArray.class);
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
		kryo.register(ClientInputState.InputType.class);
		kryo.register(Tile.Type.class);
		kryo.register(Tile.Type[].class);
		kryo.register(Tile.Type[][].class);
		kryo.register(TileTypeArray.class);
		kryo.register(Float.class);
		kryo.register(ChatNetworkObject.class);
		kryo.register(ChatLine.class);
		kryo.register(Mob.class);
		kryo.register(PositionedObject.class);
		kryo.register(Tombstone.class);
		kryo.register(Item.class);
		kryo.register(Stairs.class);
		kryo.register(Projectile.class);
		kryo.register(HealingPotion.class);
	}
}
