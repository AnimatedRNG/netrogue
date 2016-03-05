package animated.spferical.netrogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
	HashMap<String, Texture> spriteMaps = new HashMap<>();
	float animationTime = 0.5f;
	List<Animation> animations = new ArrayList<>();

	public Assets() {
		animations.add(
			loadAnimationFromBasePath("DawnLike/Characters/Player", 0, 0));
	}

	public Animation loadAnimationFromBasePath(String basePath,
			int row, int col) {
		return loadAnimation(basePath + "0.png", basePath + "1.png", row, col);
	}

	public Animation loadAnimation(String file1, String file2,
			int row, int col) {
		TextureRegion region1 = loadTextureRegion(file1, row, col);
		TextureRegion region2 = loadTextureRegion(file2, row, col);
		TextureRegion regions[] = {region1, region2};
		Array<TextureRegion> keyFrames = new Array<>(regions);
		return new Animation(animationTime, keyFrames,
				Animation.PlayMode.LOOP);
	}

	public TextureRegion loadTextureRegion(String file, int row, int col) {
		if (!spriteMaps.containsKey(file)) {
			spriteMaps.put(file, new Texture(Gdx.files.internal(file)));
		}
		return new TextureRegion(spriteMaps.get(file),
				col * 64, row * 64, 64, 64);
	}
}
