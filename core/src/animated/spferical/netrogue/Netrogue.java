package animated.spferical.netrogue;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class Netrogue extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Assets assets;
	long startTime;
	
	@Override
	public void create () {
		assets = new Assets();
		batch = new SpriteBatch();
		startTime = TimeUtils.millis();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		long currTime = TimeUtils.millis();
		batch.begin();
		float animationTime = (currTime - startTime) / 1000f;
		batch.draw(assets.animations.get(0).getKeyFrame(animationTime), 0, 0);
		batch.end();
	}
}
