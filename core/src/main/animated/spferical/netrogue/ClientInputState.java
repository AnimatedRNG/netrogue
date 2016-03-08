package animated.spferical.netrogue;

import java.io.Serializable;

public class ClientInputState implements Serializable {

	private static final long serialVersionUID = -8444092253112268601L;

	boolean moveLeft = false;
	boolean moveRight = false;
	boolean moveUp = false;
	boolean moveDown = false;

	// offsets from the player, in tiles
	int mouseXOffset = 0;
	int mouseYOffset = 0;

	boolean mouseClicked = false;
	
	// Use this for login, chat, and other player
	// commands
	String stringInput = null;
}
