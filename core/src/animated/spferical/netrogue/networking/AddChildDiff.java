package animated.spferical.netrogue.networking;
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
			return false;
		newObject.putChild(this.ID, this.child);
		return true;
	}
	
	private Long ID;
	private NetworkObject child;
}
