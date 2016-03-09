package animated.spferical.netrogue.world;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.MapGenerator;
import animated.spferical.netrogue.networking.NetworkObject;

public class GameState extends NetworkObject {

	private static final long serialVersionUID = 7697712764297546665L;

	public GameState() {
		super();
	}

	public void setupGame() {
		Level level = new Level(1, MapGenerator.mapHeight,
				MapGenerator.mapWidth);
		MapGenerator.generateMap(level);
		putChild(level);
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

	public Level findLevel(int levelNum) {
		for (NetworkObject obj : getAllChildren().values()) {
			if (obj instanceof Level) {
				Level level = (Level) obj;
				if (levelNum == level.getNumber()) {
					return level;
				}
			}
		}
		return null;
	}
}
