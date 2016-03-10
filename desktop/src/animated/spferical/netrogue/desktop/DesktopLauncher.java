package animated.spferical.netrogue.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import animated.spferical.netrogue.Netrogue;

public class DesktopLauncher {
	
	public final static float WIDTH = 0.8f;
	public final static float HEIGHT = 0.8f;
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int) (LwjglApplicationConfiguration.
				getDesktopDisplayMode().width * WIDTH);
		config.height = (int) (LwjglApplicationConfiguration.
				getDesktopDisplayMode().height * HEIGHT);
		new LwjglApplication(new Netrogue(), config);
	}
}