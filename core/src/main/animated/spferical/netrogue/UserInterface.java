package animated.spferical.netrogue;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class UserInterface {
	Animation hp1, hp2, hp3, hpfull, ap1, ap2, ap3, apfull;
	Animation barLeft, barMiddle, barRight;
	long startTime;
	SpriteBatch batch;
	Camera camera;
	Viewport viewport;

	public UserInterface() {
		hp1 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 9);
		hp2 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 8);
		hp3 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 7);
		hpfull = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 6);

		ap1 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 9);
		ap2 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 8);
		ap3 = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 7);
		apfull = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 6);

		barLeft = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 6);
		barMiddle = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 7);
		barRight = Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 8);
		startTime = TimeUtils.millis();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(1024, 768, camera);
	}

	public void draw() {
		//TODO: have player's health + ap passed in
		int tileSize = Constants.tileSize;
		int hp = 7;
		int maxHP = 10;
		float hpFraction = (float) hp / maxHP;
		float apFraction = 0.1f;
		long currTime = TimeUtils.millis();
		float animationTime = (currTime - startTime) / 1000f;
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// draw the fractions of hp
		for (int i = 0; i < hpFraction * 4; i++) {
			float baseFraction = i/4f;
			if (hpFraction >= baseFraction + .25) {
				batch.draw(hpfull.getKeyFrame(animationTime),
						i * tileSize, tileSize);
			} else if (hpFraction >= baseFraction + 3f/16) {
				batch.draw(hp3.getKeyFrame(animationTime),
						i * tileSize, tileSize);
			} else if (hpFraction >= baseFraction + 1f/8) {
				batch.draw(hp2.getKeyFrame(animationTime),
						i * tileSize, tileSize);
			} else if (hpFraction >= baseFraction + 1f/16) {
				batch.draw(hp1.getKeyFrame(animationTime),
						i * tileSize, tileSize);
			}
			if (apFraction >= baseFraction + .25) {
				batch.draw(apfull.getKeyFrame(animationTime), i * tileSize, 0);
			} else if (apFraction >= baseFraction + 3f/16) {
				batch.draw(ap3.getKeyFrame(animationTime), i * tileSize, 0);
			} else if (apFraction >= baseFraction + 1f/8) {
				batch.draw(ap2.getKeyFrame(animationTime), i * tileSize, 0);
			} else if (apFraction >= baseFraction + 1f/16) {
				batch.draw(ap1.getKeyFrame(animationTime), i * tileSize, 0);
			}
		}
		batch.end();

		// draw the bar containers
		batch.begin();
		batch.draw(barLeft.getKeyFrame(animationTime), 0, 0);
		batch.draw(barMiddle.getKeyFrame(animationTime), 1 * tileSize, 0);
		batch.draw(barMiddle.getKeyFrame(animationTime), 2 * tileSize, 0);
		batch.draw(barRight.getKeyFrame(animationTime), 3 * tileSize, 0);

		batch.draw(barLeft.getKeyFrame(animationTime), 0, tileSize);
		batch.draw(barMiddle.getKeyFrame(animationTime),
				1 * tileSize, tileSize);
		batch.draw(barMiddle.getKeyFrame(animationTime),
				2 * tileSize, tileSize);
		batch.draw(barRight.getKeyFrame(animationTime),
				3 * tileSize, tileSize);
		batch.end();
	}

	public void handleResize(int width, int height) {
		viewport.update(width, height, true);
	}
}
