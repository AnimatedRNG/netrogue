package animated.spferical.netrogue.world;

import animated.spferical.netrogue.networking.NetworkObject;

public class HealingPotion extends PositionedObject implements Actor {

	private static final long serialVersionUID = 4405271447735264375L;

	public HealingPotion() {}

	public HealingPotion(int x, int y) {
		super("healing potion", x, y);
	}

	@Override
	public void onUpdate(GameState gameState, float dt) {
		for (NetworkObject obj : gameState.getAllChildrenOfType(Player.class, false)) {
			Player p = (Player) obj;
			if (p.getX() == getX() && p.getY() == getY()) {
				int hp = (int) p.get("hp");
				int maxHP = (int) p.calculateMaxHP((int) p.get("characterLevel"));
				if (hp < maxHP) {
					p.put("hp", maxHP);
					put("dead", true);
				}
			}
		}

	}

	@Override
	public void onDeath(GameState gameState) {
	}
}
