package animated.spferical.netrogue;

import com.badlogic.gdx.Game;

public class Netrogue extends Game {
	Assets assets;
	
	@Override
	public void create () {
		Assets.load();
		setScreen(new MenuScreen());
	}
}
