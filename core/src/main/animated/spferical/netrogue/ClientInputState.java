package animated.spferical.netrogue;

import java.io.Serializable;

public class ClientInputState implements Serializable {

	private static final long serialVersionUID = -8444092253112268601L;
	
	public enum InputType {
		SELECT_OPTION, SELECT_ITEM, REPORT_VERSION
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
					this.theta == otherInputState.theta &&
					this.mouseClicked == otherInputState.mouseClicked &&
					this.pickUpItem == otherInputState.pickUpItem &&
					this.checkEquivalence(this.stringInput, 
							otherInputState.stringInput) &&
					this.checkEquivalence(this.inputType, 
							otherInputState.inputType) &&
					this.intInput == otherInputState.intInput)
			{
				return true;
			}
			else
				return false;
		}
	}
	
	public boolean checkEquivalence(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return true;
		else if (obj1 == null || obj2 == null)
			return false;
		else
			return obj1.equals(obj2);
	}
	
	public void resetAll() {
		this.moveLeft = false;
		this.moveRight = false;
		this.moveUp = false;
		this.moveDown = false;
		this.pickUpItem = false;
		
		this.theta = 0;
		
		this.mouseClicked = false;
		
		this.stringInput = null;
		this.intInput = 0;
		this.inputType = null;
	}

	public boolean moveLeft = false;
	public boolean moveRight = false;
	public boolean moveUp = false;
	public boolean moveDown = false;
	public boolean pickUpItem = false;

	// offsets from the player, in tiles
	public int theta;

	public boolean mouseClicked = false;
	
	// Use this for login, chat, and other player
	// commands
	public String stringInput = null;
	
	// Used for handling any generic integer input
	// back to the server
	public int intInput;
	
	public InputType inputType = null;
}
