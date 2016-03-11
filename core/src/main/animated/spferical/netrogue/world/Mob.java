package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Mob extends NetworkObject implements Actor {

	private static final long serialVersionUID = 8074582393481611122L;

	public Mob() {
	}

	public Mob(String type, int x, int y, int maxHP, int damage, float moveSpeed) {
		put("type", type);
		put("x", x);
		put("y", y);
		put("hp", maxHP);
		put("maxHP", maxHP);
		put("damage", damage);
		put("moveSpeed", moveSpeed);
	}

	@Override
	public void onUpdate(GameState gameState, float dt) {
		// TODO Auto-generated method stub

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
