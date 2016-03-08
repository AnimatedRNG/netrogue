package animated.spferical.netrogue;

import java.io.Serializable;

public class ClientInputState implements Serializable {

	private static final long serialVersionUID = -8444092253112268601L;
	
	public ClientInputState() {
		
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ClientInputState))
			return false;
		else
		{
			ClientInputState otherInputState = 
					((ClientInputState) other);
			if (this.moveUp == otherInputState.moveUp &&
					this.moveDown == otherInputState.moveDown &&
					this.moveLeft == otherInputState.moveLeft &&
					this.moveRight == otherInputState.moveRight &&
					this.mouseXOffset == otherInputState.mouseXOffset &&
					this.mouseYOffset == otherInputState.mouseYOffset &&
					this.mouseClicked == otherInputState.mouseClicked)
			{
					if (this.stringInput == null ^ otherInputState.stringInput == null)
						return false;
					else
					{
						if (this.stringInput == null && otherInputState.stringInput == null)
							return true;
						else
							return this.stringInput.equals(otherInputState.stringInput);
					}
			}
			else
				return false;
		}
	}

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
