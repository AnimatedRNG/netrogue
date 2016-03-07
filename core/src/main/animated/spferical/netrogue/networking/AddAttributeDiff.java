package animated.spferical.netrogue.networking;

/**
 * Diff to add a single attribute field.
 * 
 * Will throw an exception if the attribute exists.
 * 
 * @author srinivas
 */
public class AddAttributeDiff extends Diff {
	
	public AddAttributeDiff() {
		super(0, 0);
	}

	public AddAttributeDiff(long ID, long newUpdate, String name, Object value) {
		super(newUpdate, ID);
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (newObject.has(this.name))
			return false;
		newObject.put(this.name, this.value);
		return true;
	}
	
	private String name;
	private Object value;
}
