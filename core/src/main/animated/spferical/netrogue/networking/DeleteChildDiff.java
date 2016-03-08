package animated.spferical.netrogue.networking;

import com.badlogic.gdx.Gdx;

/**
 * Diff to delete a single child
 * 
 * Will throw an exception if the attribute does not
 * exist.
 * 
 * @author srinivas
 */
public class DeleteChildDiff extends Diff {
	
	public DeleteChildDiff() {
		super(0, 0);
	}

	public DeleteChildDiff(Long parentID, long newUpdate, Long ID) {
		super(newUpdate, parentID);
		this.ID = ID;
	}
	
	@Override
	public boolean onApply(NetworkObject old) {
		NetworkObject newObject = old;
		if (!newObject.hasChild(this.ID))
		{
			Gdx.app.error("Invalid Diff", "DeleteChildDiff reports that child does not exist!");
			return false;
		}
		newObject.removeChild(this.ID);
		return true;
	}
	
	private Long ID;
}
