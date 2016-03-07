package animated.spferical.netrogue.networking;

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
			return false;
		newObject.remove(this.name);
		return true;
	}
	
	private String name;
}