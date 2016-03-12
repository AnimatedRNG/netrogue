package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class Mob extends PositionedObject implements Actor {

	private static final long serialVersionUID = 8074582393481611122L;

	public Mob() {
	}

	public Mob(String type, int x, int y, int maxHP, int XP, int damage, float moveSpeed, float attackSpeed) {
		super(type, x, y);
		put("hp", maxHP);
		put("maxHP", maxHP);
		put("xp", XP);
		put("damage", damage);
		put("moveSpeed", moveSpeed);
		put("attackSpeed", attackSpeed);
		put("timeSinceLastAction", 0.0f);
	}

	@Override
	public void onUpdate(GameState gameState, float dt) {
		// attack player if he's right next to us
		float timeSinceLastAction = getTimeSinceLastAction();
		timeSinceLastAction += dt;
		put("timeSinceLastAction", timeSinceLastAction);
		float moveSpeed = (float) get("moveSpeed");
		if (timeSinceLastAction >= moveSpeed) {
			// we can move!
			makeMove(gameState);
		}

	}

	public void makeMove(GameState gameState) {
		// can we see any players nearby?
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof Player) {
				Player p = (Player) obj;
				// is the player close enough to us?
				double distanceSquared = (Math.pow(p.getX() - getX(), 2) + Math.pow(p.getY() - getY(), 2));
				if (distanceSquared <= 1 && canAttackYet() 
						&& this.get("level").equals(p.get("level"))) {
					// we can attack player!
					p.takeDamage((int) get("damage"), (String) get("type"));
					resetTimeSinceLastAction();
					return;
				} else if (distanceSquared > 1 && distanceSquared < 25
						&& canMoveYet()) {
					if (moveTowards(p.getX(), p.getY(), gameState)) {
						resetTimeSinceLastAction();
						return;
					}
				}
			}
		}
	}

	public void resetTimeSinceLastAction() {
		put("timeSinceLastAction", 0.0f);
	}

	public boolean canMoveYet() {
		return (float) get("moveSpeed") < getTimeSinceLastAction();
	}

	public boolean canAttackYet() {
		return (float) get("attackSpeed") < getTimeSinceLastAction();
	}

	public boolean moveTowards(int x, int y, GameState gameState) {
		int dx = x - getX();
		int dy = y - getY();
		if (dx == 0 && dy == 0) {
			return false;
		}
		// normalize direction values
		int xdir, ydir;
		if (dx < 0) xdir = -1;
		else if (dx > 0) xdir= 1;
		else xdir = 0;
		if (dy < 0) ydir = -1;
		else if (dy > 0) ydir = 1;
		else ydir = 0;
		if (Math.abs(dx) > Math.abs(dy)) {
			// try moving x, then moving y
			if (tryToMove(xdir, 0, gameState) ||
					tryToMove(0, ydir, gameState))
				return true;
		} else {
			// try moving y, then moving x
			if (tryToMove(0, ydir, gameState) ||
					tryToMove(xdir, 0, gameState))
				return true;
		}
		return false;
	}

	public float getTimeSinceLastAction() {
		return (float) get("timeSinceLastAction");
	}

	public boolean tryToMove(int dx, int dy, GameState gameState) {
		int x = getX() + dx;
		int y = getY() + dy;
		Level level = (Level) gameState.searchChildren(parent);
		if (!level.checkOccupied(y, x)
				&& level.checkMobCollision(y, x) == null) {
			// we can move!
			setX(x);
			setY(y);
			return true;
		}
		return false;
	}

	public void takeDamage(int damage) {
		int hp = ((int) get("hp")) - damage;
		if (hp <= 0) {
			put("dead", true);
		}
		put("hp", hp);
	}
	
	@Override
	public void onDeath(GameState gameState) {
		// TODO place corpse
	}
}
