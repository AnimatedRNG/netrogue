package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Mob extends NetworkObject implements Actor {

	private static final long serialVersionUID = 8074582393481611122L;

	public Mob() {
	}

	public Mob(String type, int x, int y, int maxHP, int XP, int damage, float moveSpeed) {
		put("type", type);
		put("x", x);
		put("y", y);
		put("hp", maxHP);
		put("maxHP", maxHP);
		put("xp", XP);
		put("damage", damage);
		put("moveSpeed", moveSpeed);
		put("timeSinceLastAction", 0.0f);
	}

	@Override
	public void onUpdate(GameState gameState, float dt) {
		// attack player if he's right next to us
		float timeSinceLastAction = (float) get("timeSinceLastAction");
		timeSinceLastAction += dt;
		put("timeSinceLastAction", timeSinceLastAction);
		float moveSpeed = (float) get("moveSpeed");
		if (timeSinceLastAction >= moveSpeed) {
			// we can move!
			makeMove(gameState);
			put("timeSinceLastAction", 0.0f);
		}

	}

	public void makeMove(GameState gameState) {
		// can we see any players nearby?
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Player) {
				Player p = (Player) obj;
				// is the player close enough to us?
				double distanceSquared = (Math.pow(p.getX() - getX(), 2) + Math.pow(p.getY() - getY(), 2));
				if (distanceSquared <= 1) {
					// we can attack player!
					p.takeDamage((int) get("damage"));
				} else if (distanceSquared < 5) {
					moveTowards(p.getX(), p.getY());
					return;
				}
			}
		}
	}

	public void moveTowards(int x, int y) {
		// TODO: simple pathfinding
	}

	public int getX() {
		return (int) get("x");
	}

	public int getY() {
		return (int) get("y");
	}

	public void setX(int x) {
		put("x", x);
	}

	public void setY(int y) {
		put("y", y);
	}

	public void takeDamage(int damage) {
		put("hp", ((int) get("hp")) - damage);
	}
}
