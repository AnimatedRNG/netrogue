package animated.spferical.netrogue.world;

public class Stairs extends PositionedObject {
	
	private static final long serialVersionUID = -8601274277990687047L;

	public Stairs() {
		super();
	}

	public Stairs(String type, int targetLevel, int x, int y) {
		super(type, x, y);
		put("targetLevel", targetLevel);
		put("renderLower", true);
	}
}
