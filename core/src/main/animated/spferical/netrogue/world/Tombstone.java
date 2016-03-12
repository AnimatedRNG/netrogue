package animated.spferical.netrogue.world;

public class Tombstone extends PositionedObject {

	private static final long serialVersionUID = -4389300399909929070L;

	public Tombstone() {}

	public Tombstone(int x, int y, String message) {
		super("tombstone", x, y);
		put("message", message);
	}
}
