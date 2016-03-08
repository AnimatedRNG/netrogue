package animated.spferical.netrogue.networking;

import com.esotericsoftware.minlog.Log;

/**
 * Diff to add a single child
 * 
 * Will throw an exception if the attribute does not
 * exist.
 * 
 * @author srinivas
 */
public class AddChildDiff extends Diff {
	
	public AddChildDiff() {
		super(0, 0);
	}

	public AddChildDiff(long parentID, long newUpdate, Long ID, NetworkObject child) {
		super(newUpdate, parentID);
		this.ID = ID;
		this.child = child;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (newObject.hasChild(this.ID))
		{
			Log.error("Invalid Diff", "AddChildDiff reports that child already exists!");
			return false;
		}
		newObject.putChild(this.child);
		return true;
	}
	
	private Long ID;
	private NetworkObject child;
}
