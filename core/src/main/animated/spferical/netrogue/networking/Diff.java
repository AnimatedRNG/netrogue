package animated.spferical.netrogue.networking;

import com.esotericsoftware.minlog.Log;

/**
 * A Diff signifies an in-place modification of state
 * 
 * @author srinivas
 *
 */
public abstract class Diff {
	
	public int connectionID;
	public boolean actuallyDoSomething;
	
	public Diff(long newUpdate, long ID) {
		this.actuallyDoSomething = true;
		this.newUpdate = newUpdate;
		this.targetID = ID;
	}
	
	/*
	 * Updates the object if it is able to update the
	 * object
	 */
	public boolean apply(NetworkObject old) {
		if (targetID == old.ID)
		{
			if (old.lastUpdate + 1 == newUpdate)
			{
				old.lastUpdate++;
				return this.onApply(old);
			}
			else
			{
				Log.error("Update numbers do not add up. Old update is "
					+ old.lastUpdate + " and new update is " + newUpdate);
				Log.warn("Applying update regardless. May cause unintended consequences.");
				old.lastUpdate = newUpdate;
				this.onApply(old);
				return false;
			}
		}
		else
		{
			boolean result = false;
			for (NetworkObject child : old.getAllChildren().values())
			{
				if (child.ID == targetID && actuallyDoSomething)
					result = this.apply(child);
			}
			return result;
		}
	}
	
	public abstract boolean onApply(NetworkObject old);
	
	protected long newUpdate;
	protected long targetID;
}
