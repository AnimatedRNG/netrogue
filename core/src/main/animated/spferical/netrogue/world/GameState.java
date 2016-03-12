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
	}

	public void setupGame() {
		this.put("lastTimeUpdate", System.currentTimeMillis());
		// generate levels
		for (int i = 1; i <= Constants.LEVEL_NUM; i++)
		{
			Level level = new Level(i, MapGenerator.mapHeight,
					MapGenerator.mapWidth);
			MapGenerator.generateMap(level);
			putChild(level);
		}
		
		new Spawner().spawnDownstairs(this);

		// create chat directory
		ChatNetworkObject chat = new ChatNetworkObject();
		putChild(chat);

	}

	public void removeFromTree(NetworkObject obj) {
		System.out.println("Deleting " + obj.ID);
		long parentID = obj.parent;
		NetworkObject parent = searchChildren(parentID);
		parent.removeChild(obj.ID);
	}
	
	// Handle player input
	public void handlePlayerInput(Player player, ClientInputState input, float dt) {
		if (player == null)
			return;
		
		// All your GameState input code here
		player.put("timeSinceLastAction", ((float) player.get("timeSinceLastAction")) + dt);
		Level level = this.getLevelByNumber(player.getDungeonLevel());
		if ((float) player.get("timeSinceLastAction") > Constants.actionDelay) {
			if (input.moveUp || input.moveDown || input.moveLeft || input.moveRight) {
				int AP = (int) player.get("ap");
				boolean lowAP = AP <= 0;
				int currentX = player.getX();
				int currentY = player.getY();
				int newX = currentX;
				int newY = currentY;
				if (input.moveUp) {
					newY = currentY + 1;
				} else if (input.moveLeft) {
					newX = currentX - 1;
				} else if (input.moveDown) {
					newY = currentY - 1;
				} else if (input.moveRight) {
					newX = currentX + 1;
				}
				
				if (!level.checkOccupied(newY, newX) && !lowAP) {
					Mob m = level.checkMobCollision(newY, newX);
					if (m != null) {
						Log.info("Player attacks mob at " + newX + ", " + newY);
						// attack the mob!
						m.takeDamage(player.calculateMeleeDamage());
						if (((boolean) m.check("dead"))) {
							player.onKillMob(m);
							Log.info("Player kills mob at " + newX + ", " + newY);
						}
						player.put("timeSinceLastAction", 0.0f);
						if (AP > 0)
							player.put("ap", AP - 1);
					} else {
						// nothing here, so move
						player.setX(newX);
						player.setY(newY);
						player.put("timeSinceLastAction", 0.0f);
						if (AP > 0)
							player.put("ap", AP - 1);
						for (NetworkObject obj : level.getAllChildren().values()) {
							if (obj instanceof Tombstone) {
								// tombstone messages
								Tombstone ts = (Tombstone) obj;
								if (ts.getX() == player.getX() && ts.getY() == player.getY()) {
									String message = (String) obj.get("message");
									player.addPlayerMessage(message);
								}
							}
						}
					}
				}
			} else if (input.pickUpItem) {
				for (NetworkObject obj : level.getAllChildren().values()) {
					if (obj instanceof Item) {
						Item item = (Item) obj;
						if (item.getX() == player.getX()
								&& item.getY() == player.getY()) {
							level.removeChild(item.ID);
							String itemSlot = (String) item.get("itemSlot");
							String equippedItem = (String) player.get(itemSlot);
							if (equippedItem != null) {
								// drop the player's equipped item
								Item newItem = new Item(equippedItem, itemSlot,
										item.getX(), item.getY());
								level.putChild(newItem);
							}
							// equip the picked-up item
							player.put(itemSlot, item.get("type"));
						}
					}
				}
			}
			if (input.mouseClicked && (input.theta >= 0 && input.theta < 360))
			{
				player.put("timeSinceLastAction", 0.0f);
				String slotType = Constants.slots[(int) player.get("selection")];
				
				if (player.has(slotType))
				{
					if (Constants.spellInfos.containsKey((String) player.get(slotType)))
					{
						Log.info("Game Logic", "Player should fire spell");
						player.castSpell(this, input.theta, 
								Constants.spellInfos.get((String) player.get(slotType)));
					}
				}
			}
			
			player.onPlayerInput(input);

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
	
	/**
	 * Currently skips chunks for the sake of efficiency
	 * 
	 * @param object object whose children we need to update
	 */
	public void updateAllChildren(NetworkObject object, float dt) {
		for (NetworkObject child : object.getAllChildren().values())
			if (child instanceof Actor) {
				((Actor) child).onUpdate(this, dt);
				if (child.check("dead")) {
					((Actor) child).onDeath(this);
					removeFromTree(child);
				}
			}
		for (NetworkObject child : object.getAllChildren().values())
		{
			if (!(child instanceof Chunk))
				updateAllChildren(child, dt);
		}
	}
}
