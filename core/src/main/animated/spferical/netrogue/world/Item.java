package animated.spferical.netrogue.world;

public class Item extends PositionedObject {

	private static final long serialVersionUID = 2055979242400576214L;

	public Item() {}

	public Item(String type, String itemSlot, int x, int y) {
		super(type, x, y);
		put("itemSlot", itemSlot);
		put("renderLower", true);
	}
}
