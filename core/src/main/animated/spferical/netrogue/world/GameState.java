package animated.spferical.netrogue.world;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.ChatLine;
import animated.spferical.netrogue.ChatNetworkObject;
import animated.spferical.netrogue.ClientInputState;
import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.MapGenerator;
import animated.spferical.netrogue.networking.NetworkObject;

public class GameState extends NetworkObject {

	private static final long serialVersionUID = 7697712764297546665L;

	public GameState() {
		super();
		setupGame();
	}

	public void setupGame() {
		// generate levels
		Level level = new Level(1, MapGenerator.mapHeight,
				MapGenerator.mapWidth);
		MapGenerator.generateMap(level);
		putChild(level);

		// create chat directory
		ChatNetworkObject chat = new ChatNetworkObject();
		putChild(chat);

	}
	
	// Handle player input
	public void handlePlayerInput(Player player, ClientInputState input, float dt) {
		// All your GameState input code here
		Log.info("Tree", "" + this);
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

			if (input.stringInput != null) {
				// player sent message
				ChatLine chatLine = new ChatLine(input.stringInput,
						System.currentTimeMillis());

				for (NetworkObject obj : getAllChildren().values()) {
					if (obj instanceof ChatNetworkObject) {
						obj.putChild(chatLine);
						break;
					}
				}
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
