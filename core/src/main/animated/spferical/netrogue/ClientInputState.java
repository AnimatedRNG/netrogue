package animated.spferical.netrogue;

import java.io.Serializable;

public class ClientInputState implements Serializable {

	private static final long serialVersionUID = -8444092253112268601L;
	
	public enum InputType {
		
	};
	
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
	
	public void resetAll() {
		this.moveLeft = false;
		this.moveRight = false;
		this.moveUp = false;
		this.moveDown = false;
		
		this.mouseXOffset = 0;
		this.mouseYOffset = 0;
		
		this.mouseClicked = false;
		
		this.stringInput = null;
	}

	public boolean moveLeft = false;
	public boolean moveRight = false;
	public boolean moveUp = false;
	public boolean moveDown = false;

	// offsets from the player, in tiles
	public int mouseXOffset = 0;
	public int mouseYOffset = 0;

	public boolean mouseClicked = false;
	
	// Use this for login, chat, and other player
	// commands
	public String stringInput = null;
}
