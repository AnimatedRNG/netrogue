package animated.spferical.netrogue.world;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.networking.NetworkObject;

public class GameState extends NetworkObject {

	private static final long serialVersionUID = 7697712764297546665L;

	public GameState() {
		super();
	}
	
	// Handle player input
	public void handlePlayerInput(Player player) {
		if (!player.has("input"))
		{
			Log.warn("Input", "Client does not have input state! Skipping input");
			return;
		}
		
		ClientInputState input = (ClientInputState) player.get("input");
		
		// All your GameState input code here
	}
}
