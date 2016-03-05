package animated.spferical.netrogue.networking;

/**
 * A Diff signifies an in-place modification of state
 * 
 * @author srinivas
 *
 */
public abstract class Diff {
	
	public Diff(int newUpdate) {
		this.newUpdate = newUpdate;
	}

	/*
	 * Updates the object if it is able to update the
	 * object
	 */
	public boolean apply(NetworkObject old) {
		if (old.lastUpdate == newUpdate - 1)
		{
			this.onApply(old);
			return true;
		}
		else
			return false;
	}
	
	public abstract boolean onApply(NetworkObject old);
	
	protected int newUpdate;
}
