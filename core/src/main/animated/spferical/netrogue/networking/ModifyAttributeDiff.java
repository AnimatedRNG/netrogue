package animated.spferical.netrogue.networking;

import com.badlogic.gdx.Gdx;

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
		super(0, 0);
	}

	public ModifyAttributeDiff(Long ID, long newUpdate, String name, Object value) {
		super(newUpdate, ID);
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (!newObject.has(this.name))
		{
			Gdx.app.error("Invalid Diff", "ModifyAttributeDiff reports that attribute does not exist!");
			return false;
		}
		newObject.put(this.name, this.value);
		return true;
	}
	
	private String name;
	private Object value;
}
