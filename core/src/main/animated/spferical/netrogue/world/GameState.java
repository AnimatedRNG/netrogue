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
	public void handlePlayerInput(Player player, ClientInputState input, float dt) {
		// All your GameState input code here
		player.put("timeSinceLastAction", ((float) player.get("timeSinceLastAction")) + dt);
		Level level = this.getLevelByNumber(player.getDungeonLevel());
		if ((float) player.get("timeSinceLastAction") > Constants.actionDelay) {
			int currentX = player.getX();
			int currentY = player.getY();
			if (input.moveUp) {
				int newY = player.getY() + 1;
				if (!level.checkOccupied(newY, currentX))
					player.setY(newY);
				player.put("timeSinceLastAction", 0.0f);
			} else if (input.moveLeft) {
				int newX = player.getX() - 1;
				if (!level.checkOccupied(currentY, newX))
					player.setX(newX);
				player.put("timeSinceLastAction", 0.0f);
			} else if (input.moveDown) {
				int newY = player.getY() - 1;
				if (!level.checkOccupied(newY, currentX))
					player.setY(newY);
				player.put("timeSinceLastAction", 0.0f);
			} else if (input.moveRight) {
				int newX = player.getX() + 1;
				if (!level.checkOccupied(currentY, newX))
					player.setX(newX);
				player.put("timeSinceLastAction", 0.0f);
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
