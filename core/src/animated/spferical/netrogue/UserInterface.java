package animated.spferical.netrogue;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

public class UserInterface {
	Animation hp1, hp2, hp3, hpfull, ap1, ap2, ap3, apfull;
	Animation barLeft, barMiddle, barRight;
	long startTime;
	SpriteBatch batch;

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
	}

	public void draw() {
		//TODO: have player's health + ap passed in
		int hp = 7;
		int maxHP = 10;
		float hpFraction = (float) hp / maxHP;
		float apFraction = 0.1f;
		long currTime = TimeUtils.millis();
		float animationTime = (currTime - startTime) / 1000f;
		batch.begin();
		// draw the fractions of hp
		for (int i = 0; i < hpFraction * 4; i++) {
			float baseFraction = i/4f;
			if (hpFraction >= baseFraction + .25) {
				batch.draw(hpfull.getKeyFrame(animationTime), i * 64, 64);
			} else if (hpFraction >= baseFraction + 3f/16) {
				batch.draw(hp3.getKeyFrame(animationTime), i * 64, 64);
			} else if (hpFraction >= baseFraction + 1f/8) {
				batch.draw(hp2.getKeyFrame(animationTime), i * 64, 64);
			} else if (hpFraction >= baseFraction + 1f/16) {
				batch.draw(hp1.getKeyFrame(animationTime), i * 64, 64);
			}
			if (apFraction >= baseFraction + .25) {
				batch.draw(apfull.getKeyFrame(animationTime), i * 64, 0);
			} else if (apFraction >= baseFraction + 3f/16) {
				batch.draw(ap3.getKeyFrame(animationTime), i * 64, 0);
			} else if (apFraction >= baseFraction + 1f/8) {
				batch.draw(ap2.getKeyFrame(animationTime), i * 64, 0);
			} else if (apFraction >= baseFraction + 1f/16) {
				batch.draw(ap1.getKeyFrame(animationTime), i * 64, 0);
			}
		}
		batch.end();

		// draw the bar containers
		batch.begin();
		batch.draw(barLeft.getKeyFrame(animationTime), 0, 0);
		batch.draw(barMiddle.getKeyFrame(animationTime), 1 * 64, 0);
		batch.draw(barMiddle.getKeyFrame(animationTime), 2 * 64, 0);
		batch.draw(barRight.getKeyFrame(animationTime), 3 * 64, 0);

		batch.draw(barLeft.getKeyFrame(animationTime), 0, 64);
		batch.draw(barMiddle.getKeyFrame(animationTime), 1 * 64, 64);
		batch.draw(barMiddle.getKeyFrame(animationTime), 2 * 64, 64);
		batch.draw(barRight.getKeyFrame(animationTime), 3 * 64, 64);
		batch.end();
	}
}
