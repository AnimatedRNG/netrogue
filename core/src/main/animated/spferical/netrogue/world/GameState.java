package animated.spferical.netrogue.world;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.networking.NetworkObject;

public class GameState extends NetworkObject {

	private static final long serialVersionUID = 7697712764297546665L;

	public GameState() {
		super();
	}
	
	// Handle player input
	public void handlePlayerInput(Player player, float dt) {
		if (!player.has("input"))
		{
			Log.warn("Input", "Client does not have input state! Skipping input");
			return;
		}
		
		ClientInputState input = (ClientInputState) player.get("input");
		
		// All your GameState input code here
		player.put("timeSinceLastAction", ((float) player.get("timeSinceLastAction")) + dt);
		if ((float) player.get("timeSinceLastAction") > Constants.actionDelay) {
			if (input.moveUp) {
				player.setY(player.getY() + 1);
				player.put("timeSinceLastAction", 0);
			} else if (input.moveLeft) {
				player.setX(player.getX() - 1);
				player.put("timeSinceLastAction", 0);
			} else if (input.moveDown) {
				player.setY(player.getY() - 1);
				player.put("timeSinceLastAction", 0);
			} else if (input.moveRight) {
				player.setX(player.getX() + 1);
				player.put("timeSinceLastAction", 0);
			}
		}
	}
	
	public Level getLevelByNumber(int number) {
		for (NetworkObject child : this.getAllChildren().values())
		{
			if (child instanceof Level)
			{
				if (child.get("number").equals(number))
					return (Level) child;
			}
		}
		return null;
	}
}
