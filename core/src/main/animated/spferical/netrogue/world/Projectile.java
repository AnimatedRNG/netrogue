package animated.spferical.netrogue.world;

import java.util.List;

import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.Constants;
import animated.spferical.netrogue.networking.NetworkObject;

public class Projectile extends PositionedObject implements Actor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7582259647468803817L;

	public Projectile() {
		super();
	}

	// Assign level immediately after creating
	public Projectile(String type, float x, float y, int theta, float speed, float duration, int damage, Long caster) {
		super(type, (int) x, (int) y);
		put("lifetime", 0.0f);
		put("duration", duration);
		put("damage", damage);
		put("theta", theta);
		put("speed", speed);
		put("caster", caster);
		put("actualX", x);
		put("actualY", y);
		put("lastX", (int) x);
		put("lastY", (int) y);
	}

	@Override
	public void onUpdate(GameState gameState, float dt) {
		if (check("dead"))
			return;
		float lifetime = (float) get("lifetime") + dt;
		if (lifetime > (float) get("duration"))
			put("dead", true);
		else
		{
			put("lifetime", lifetime);
			if (!check("hasAttacked"))
			{
				Level level = gameState.getLevelByNumber((int) this.get("level"));
				List<NetworkObject> children = level.getAllChildrenOfType(Actor.class, false);
				children.addAll(gameState.getAllChildrenOfType(Player.class, false));
				for (NetworkObject actor : children)
				{
					if (!actor.has("x") || !actor.has("y"))
						continue;
					if ((int) actor.get("x") != this.getX() || (int) actor.get("y") != this.getY())
						continue;
					Player caster = (Player) gameState.searchChildren((Long) get("caster"));
					if (actor instanceof Player && actor.ID != (Long) get("caster"))
					{
						((Player) actor).takeDamage((int) get("damage"), 
								(String) caster.get("name"));
						if (((Player) actor).check("dead"))
						{
							int xp = (int) ((Player) actor).get("xp");
							caster.gainExperience(xp);
							caster.addPlayerMessage("You murdered player " + 
									((Player) actor).get("name") + " for "
									+ xp + " XP.");
						}
					}
					else if (actor instanceof Mob) {
						Mob mob = ((Mob) actor);
						mob.takeDamage((int) get("damage"));
						if (mob.check("dead"))
						{
							caster.gainExperience((int) mob.get("xp"));
							caster.addPlayerMessage("You killed a " + 
								mob.get("type") + " for "
								+ mob.get("xp") + " XP.");
						}
					}
				}
			}
			put("hasAttacked", true);
			if (lifetime > (float) get("speed"))
			{
				int theta = ((Integer) get("theta"));
				double thetaRadians = Math.toRadians((double) theta);
				double magnitude = 0.1;
				float newX = (float) get("actualX");
				float newY = (float) get("actualY");
				while ((int) newX == getX() && (int) newY == getY())
				{
					newX += (float) Math.cos(thetaRadians) * magnitude;
					newY += (float) Math.sin(thetaRadians) * magnitude;
				}
				
				Level level = gameState.getLevelByNumber((int) this.get("level"));
				
				if (newX > level.getWidth() * Constants.chunkSize || 
						newY > level.getHeight() * Constants.chunkSize 
						|| newX < 0 || newY < 0)
					return;
				
				if (level.checkOccupied((int) newY, (int) newX))
					return;
				
				Projectile child = null;
				if (!this.check("spawnedChild"))
				{
					child = new Projectile(
							(String) get("type"), newX, newY, theta,
							(float) get("speed"), (float) this.get("duration") - lifetime,
							(int) this.get("damage"), (Long) this.get("caster"));
					child.put("level", get("level"));
					child.put("lastX", this.getX());
					child.put("lastY", this.getY());
					level.putChild(child);
				}
				this.put("spawnedChild", true);
			}
		}
	}

	@Override
	public void onDeath(GameState gameState) {
		
	}

}
