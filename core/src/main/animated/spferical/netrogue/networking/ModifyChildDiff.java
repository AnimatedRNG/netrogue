package animated.spferical.netrogue.networking;

import com.esotericsoftware.minlog.Log;

/**
 * Diff to modify a single child
 * 
 * Will throw an exception if the attribute does not
 * exist.
 * 
 * This diff should theoretically never be used
 * 
 * @author srinivas
 */
public class ModifyChildDiff extends Diff {
	
	public ModifyChildDiff() {
		super(0, 0);
	}

	public ModifyChildDiff(long parentID, long newUpdate, Long ID, NetworkObject child) {
		super(newUpdate, parentID);
		this.ID = ID;
		this.child = child;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (!newObject.hasChild(this.ID))
		{
			Log.error("Invalid Diff", "ModifyChildDiff reports that child does not exist!");
			return false;
		}
		newObject.putChild(this.child);
		return true;
	}
	
	private Long ID;
	private NetworkObject child;
}
