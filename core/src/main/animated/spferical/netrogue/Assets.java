package animated.spferical.netrogue;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class Assets {
	public static final int FONT_SIZE = 50;
	
	static HashMap<String, Texture> spriteMaps = new HashMap<>();
	static float animationTime = 0.5f;
	static HashMap<String, Animation> animations = new HashMap<>();
	static HashMap<String, TextureRegion> objects = new HashMap<>();
	static FreeTypeFontGenerator fontGenerator;
	static HashMap<Integer, BitmapFont> fonts = new HashMap<>();
	static HashMap<String, TextureRegion> items = new HashMap<>();
	static Skin skin;

	public static void load() {
		animations.put(
			"player", 
			loadAnimationFromBasePath("DawnLike/Characters/Player", 0, 0));
		animations.put("worm",
			loadAnimationFromBasePath("DawnLike/Characters/Pest", 3, 0));
		animations.put("big worm",
			loadAnimationFromBasePath("DawnLike/Characters/Pest", 3, 1));
		animations.put("ant",
			loadAnimationFromBasePath("DawnLike/Characters/Pest", 4, 0));
		animations.put("beetle",
			loadAnimationFromBasePath("DawnLike/Characters/Pest", 10, 0));
		animations.put("butterfly",
			loadAnimationFromBasePath("DawnLike/Characters/Pest", 1, 3));
		animations.put("slime",
			loadAnimationFromBasePath("DawnLike/Characters/Slime", 1, 1));
		animations.put("bat",
			loadAnimationFromBasePath("DawnLike/Characters/Avian", 1, 0));
		animations.put("big bat",
			loadAnimationFromBasePath("DawnLike/Characters/Avian", 2, 0));
		animations.put("tombstone",
			loadAnimationFromBasePath("DawnLike/Objects/Decor", 17, 0));
		animations.put(
			"hp1", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 9));
		animations.put(
			"hp2", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 8));
		animations.put(
			"hp3", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 7));
		animations.put(
			"hpfull", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 1, 6));

		animations.put(
			"ap1", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 9));
		animations.put(
			"ap2", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 8));
		animations.put(
			"ap3", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 7));
		animations.put(
			"apfull", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 2, 6));

		animations.put(
			"barLeft", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 6));
		animations.put(
			"barMiddle", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 7));
		animations.put(
			"barRight", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 0, 8));
		animations.put(
			"slot", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 7, 8));
		animations.put(
			"selectedSlot", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 7, 12));
		animations.put(
			"fire", Assets.loadAnimationFromBasePath("DawnLike/Objects/Effect", 21, 0));
		animations.put(
			"firerayvert", Assets.loadAnimationFromBasePath("DawnLike/Objects/Effect", 15, 0));
		animations.put(
			"firerayhoriz", Assets.loadAnimationFromBasePath("DawnLike/Objects/Effect", 15, 1));
		animations.put(
			"fireraydiag1", Assets.loadAnimationFromBasePath("DawnLike/Objects/Effect", 15, 2));
		animations.put(
			"fireraydiag2", Assets.loadAnimationFromBasePath("DawnLike/Objects/Effect", 15, 3));
		animations.put(
			"boss", Assets.loadAnimationFromBasePath("DawnLike/Characters/Reptile", 11, 3));
		
		objects.put(
			"downstairs", Assets.loadTextureRegion("DawnLike/Objects/Tile.png", 1, 5));
		objects.put(
			"upstairs", Assets.loadTextureRegion("DawnLike/Objects/Tile.png", 1, 4));
		animations.put(
			"redbox", Assets.loadAnimationFromBasePath("DawnLike/GUI/GUI", 8, 6));

		items.put("healing potion", Assets.loadTextureRegion("DawnLike/Items/Potion.png", 0, 0));
		items.put("fire", Assets.loadTextureRegion("DawnLike/Items/Book.png", 4, 0));
		items.put("dagger", Assets.loadTextureRegion("DawnLike/Items/ShortWep.png", 0, 0));
		items.put("club", Assets.loadTextureRegion("DawnLike/Items/ShortWep.png", 2, 0));
		items.put("mace", Assets.loadTextureRegion("DawnLike/Items/ShortWep.png", 4, 0));
		items.put("sword", Assets.loadTextureRegion("DawnLike/Items/MedWep.png", 0, 0));
		items.put("axe", Assets.loadTextureRegion("DawnLike/Items/MedWep.png", 1, 1));
		items.put("double-axe", Assets.loadTextureRegion("DawnLike/Items/MedWep.png", 1, 1));

		fontGenerator = new FreeTypeFontGenerator(
			Gdx.files.internal("DawnLike/GUI/SDS_8x8.ttf"));
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		skin.add("default-font", getFont(FONT_SIZE), BitmapFont.class);
	}

	public static Animation loadAnimationFromBasePath(String basePath,
			int row, int col) {
		return loadAnimation(basePath + "0.png", basePath + "1.png", row, col);
	}

	public static Animation loadAnimation(String file1, String file2,
			int row, int col) {
		TextureRegion region1 = loadTextureRegion(file1, row, col);
		TextureRegion region2 = loadTextureRegion(file2, row, col);
		TextureRegion regions[] = {region1, region2};
		Array<TextureRegion> keyFrames = new Array<>(regions);
		return new Animation(animationTime, keyFrames,
				Animation.PlayMode.LOOP);
	}

	public static TextureRegion loadTextureRegion(String file, int row, int col) {
		if (!spriteMaps.containsKey(file)) {
			spriteMaps.put(file, new Texture(Gdx.files.internal(file)));
		}
		int tileSize = Constants.tileSize;
		return new TextureRegion(spriteMaps.get(file),
				col * tileSize, row * tileSize, tileSize, tileSize);
	}

	public static BitmapFont getFont(int size) {
		if (!fonts.containsKey(size)) {
			FreeTypeFontParameter parameter = new FreeTypeFontParameter();
			parameter.size = size;
			fonts.put(size, fontGenerator.generateFont(parameter));
		}
		return fonts.get(size);
	}
}
