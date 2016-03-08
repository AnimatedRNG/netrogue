package animated.spferical.netrogue.networking;

/**
 * A Diff signifies an in-place modification of state
 * 
 * @author srinivas
 *
 */
public abstract class Diff {
	
	public int connectionID;
	
	public Diff(long newUpdate, long ID) {
		this.newUpdate = newUpdate;
		this.targetID = ID;
	}
	
	/*
	 * Updates the object if it is able to update the
	 * object
	 */
	public boolean apply(NetworkObject old) {
		if (old.lastUpdate + 1 == newUpdate)
		{
			boolean result = true;
			if (targetID == old.ID)
				result = this.onApply(old);
			else
			{
				for (NetworkObject child : old.getAllChildren().values())
				{
					if (child.ID == targetID)
						result = this.onApply(child);
				}
			}
			old.lastUpdate++;
			return result;
		}
		else
			return false;
	}
	
	public abstract boolean onApply(NetworkObject old);
	
	protected long newUpdate;
	protected long targetID;
}
