package animated.spferical.netrogue.world;

/**
 * Any entity that does things in the game world
 * should implement onUpdate.
 * 
 * Examples include Player, monsters, and projectiles 
 * 
 * @author srinivas
 */
public interface Actor {

	/**
	 * Run once every network update on the server
	 * 
	 * The notion of dt might sound a little strange given
	 * that everything happens over a network. The implementation
	 * guarantees, however, that this time is accurate across BOTH
	 * the client and the server. This means that the last update
	 * could have occurred on the server, and the current update
	 * could be occurring on the client.
	 * 
	 * To do this, we assume that non-IO-based operations are
	 * instantaneous. 
	 * 
	 * @param gameState GameState
	 * @param dt Time elapsed since last update
	 */
	public abstract void onUpdate(GameState gameState, float dt);

	/**
	 * To be called when an actor is removed from the tree.
	 */
	public void onDeath(GameState gameState);
}
