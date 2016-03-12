package animated.spferical.netrogue.world;

public class Downstairs extends PositionedObject {
	
	private static final long serialVersionUID = -8601274277990687047L;

	public Downstairs() {
		super();
	}

	public Downstairs(String type, int x, int y) {
		super(type, x, y);
		put("renderLower", true);
	}
}
