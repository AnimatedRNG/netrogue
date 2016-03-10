package animated.spferical.netrogue.networking;

import com.esotericsoftware.minlog.Log;

/**
 * Diff to delete a single attribute field.
 * 
 * Will throw an exception if the attribute does not
 * exist.
 * 
 * @author srinivas
 */
public class DeleteAttributeDiff extends Diff {
	
	public DeleteAttributeDiff() {
		super(0, 0);
	}

	public DeleteAttributeDiff(long ID, long newUpdate, String name) {
		super(newUpdate, ID);
		this.name = name;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (!newObject.has(this.name))
		{
			Log.error("Invalid Diff", "DeleteAttributeDiff reports that attribute does not exist!");
			return false;
		}
		newObject.remove(this.name);
		return true;
	}
	
	public String name;
}
