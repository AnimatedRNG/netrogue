package animated.spferical.netrogue.world;

import com.esotericsoftware.minlog.Log;

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
			if (lifetime > (float) get("speed"))
			{
				double thetaRadians = Math.toRadians((double) ((Integer) get("theta")));
				double magnitude = 0.1;
				float newX = (float) get("actualX");
				float newY = (float) get("actualY");
				while ((int) newX == getX() && (int) newY == getY())
				{
					newX += (float) Math.cos(thetaRadians) * magnitude;
					newY += (float) Math.sin(thetaRadians) * magnitude;
				}
				
				Log.info("Projectile Physics", "Lifetime: " + lifetime);
				Log.info("Projectile Physics", "(" + newX + ", " + newY + ")");
				Level level = gameState.getLevelByNumber((int) this.get("level"));
				
				/*if (newX > level.getWidth() || newY > level.getHeight() || newX < 0 || newY < 0)
					return;*/
				
				Projectile child = new Projectile(
						(String) get("type"), newX, newY, (int) get("theta"),
						(float) get("speed"), (float) this.get("duration") - lifetime, (int) this.get("damage"), 
						(Long) this.get("caster"));
				child.put("level", get("level"));
				level.putChild(child);
			}
		}
	}

	@Override
	public void onDeath(GameState gameState) {
		
	}

}