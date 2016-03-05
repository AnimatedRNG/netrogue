package animated.spferical.netrogue.networking;

/**
 * Diff to modify a single attribute field.
 * 
 * Will throw an exception if the attribute does not
 * exist.
 * 
 * @author srinivas
 */
public class ModifyAttributeDiff extends Diff {
	
	public ModifyAttributeDiff() {
		super(0);
	}

	public ModifyAttributeDiff(int newUpdate, String name, Object value) {
		super(newUpdate);
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (!newObject.has(this.name))
			return false;
		newObject.put(this.name, this.value);
		return true;
	}
	
	private String name;
	private Object value;
}
