package animated.spferical.netrogue.networking;

/**
 * A Diff signifies an in-place modification of state
 * 
 * @author srinivas
 *
 */
public abstract class Diff {
	
	public Diff(long newUpdate, long ID) {
		this.newUpdate = newUpdate;
		this.ID = ID;
	}

	/*
	 * Updates the object if it is able to update the
	 * object
	 */
	public boolean apply(NetworkObject old) {
		if (old.lastUpdate + 1 == newUpdate)
		{
			if (ID == old.ID)
				this.onApply(old);
			else
			{
				for (NetworkObject child : old.getAllChildren().values())
				{
					if (child.ID == ID)
						this.onApply(child);
				}
			}
			old.lastUpdate++;
			return true;
		}
		else
			return false;
	}
	
	public abstract boolean onApply(NetworkObject old);
	
	protected long newUpdate;
	protected long ID;
}
