package animated.spferical.netrogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;

import animated.spferical.netrogue.networking.NetworkObject;
import animated.spferical.netrogue.networking.StringArray;
import animated.spferical.netrogue.world.GameState;
import animated.spferical.netrogue.world.Player;

public class UserInterface {
	
	public static final int MESSAGE_DURATION = 1000;
	
	Animation hp1, hp2, hp3, hpfull, ap1, ap2, ap3, apfull;
	Animation barLeft, barMiddle, barRight, slot, selectedSlot;
	long startTime;
	SpriteBatch batch;
	Camera camera;
	Viewport viewport;
	Stage stage;
	TextField chatField;
	Table chatInnerTable;
	ScrollPane chatScrollPane;
	boolean shouldScrollToChatBottom = true;

	final int chatPanelWidth = 200;
	final int chatPanelHeight = 200;
	
	Label playerMessage;
	long playerMessageTime;
	Long lastMessageID;
	HorizontalGroup serverGUI_layout;
	SelectBox<String> serverGUI;
	TextButton serverGUI_OK;
	Long serverGUI_ID;
	public int selectedIndex = -1;

	public UserInterface() {
		hp1 = Assets.animations.get("hp1");
		hp2 = Assets.animations.get("hp2");
		hp3 = Assets.animations.get("hp3");
		hpfull = Assets.animations.get("hpfull");

		ap1 = Assets.animations.get("ap1");
		ap2 = Assets.animations.get("ap2");
		ap3 = Assets.animations.get("ap3");
		apfull = Assets.animations.get("apfull");

		barLeft = Assets.animations.get("barLeft");
		barMiddle = Assets.animations.get("barMiddle");
		barRight = Assets.animations.get("barRight");

		slot = Assets.animations.get("slot");
		selectedSlot = Assets.animations.get("selectedSlot");

		startTime = TimeUtils.millis();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(1024, 768, camera);

		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);

		Table table = new Table();
		table.setFillParent(true);
		table.setDebug(true);

		chatInnerTable = new Table();
		chatInnerTable.setDebug(true);
		chatInnerTable.padBottom(10);

		chatScrollPane = new ScrollPane(chatInnerTable, Assets.skin);
		chatScrollPane.setScrollingDisabled(true, false);
		chatScrollPane.setDebug(true);

		chatField = new TextField("", Assets.skin);
		chatField.setMessageText("Enter to chat");

		table.add(chatScrollPane).left()
			.width(chatPanelWidth).height(chatPanelHeight);
		table.row();
		table.add(chatField).left().width(chatPanelWidth);
		table.bottom().right();
		stage.addActor(table);
		
		playerMessage = new Label("", Assets.skin);
		playerMessage.setFontScale(1.5f);
		playerMessageTime = -1;
		lastMessageID = 0L;
		playerMessage.setAlignment(Align.center);
		stage.addActor(playerMessage);
		
		serverGUI = new SelectBox<String>(Assets.skin);
		serverGUI.setWidth(100);
		serverGUI_OK = new TextButton("OK", Assets.skin); 
		serverGUI_OK.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				selectedIndex = serverGUI.getSelectedIndex();
				serverGUI_layout.remove();
			}
		});
		serverGUI_layout = new HorizontalGroup();
		serverGUI_layout.align(Align.center);
		serverGUI_layout.addActor(serverGUI);
		serverGUI_layout.addActor(serverGUI_OK);
		serverGUI_ID = 0L;
	}

	public void draw(GameState gameState, long playerID) {
		Player player = (Player) gameState.searchChildren(playerID); 
		float hpFraction, apFraction;
		if (player == null) {
			hpFraction = apFraction = 0;
		} else {
			int hp = (int) player.get("hp");
			int maxHP = player.calculateMaxHP((int) player.get("characterLevel"));
			int ap = (Integer) player.get("ap");
			hpFraction = (float) hp / maxHP;
			apFraction = (float) ap / (float) player.calculateMaxAP((int) player.get("characterLevel"));
		}
		
		if (player != null && player.has("playerMessage") && this.playerMessageTime == -1 &&
				!(player.get("playerMessageID").equals(lastMessageID)))
		{
			Log.info("Client GUI", "Rendering message for player. Last message ID: " + lastMessageID);
			String message = (String) player.get("playerMessage");
			this.renderPlayerMessage(message);
			this.playerMessageTime = System.currentTimeMillis();
			this.lastMessageID = (Long) player.get("playerMessageID");
		}
		else if (this.playerMessageTime != -1 && 
				System.currentTimeMillis() - this.playerMessageTime > MESSAGE_DURATION)
		{
			Log.info("Client GUI", "No longer rendering message for player. Last Message ID: " + lastMessageID);
			this.playerMessageTime = -1;
			this.renderPlayerMessage("");
		}
		
		if (player != null && player.has("playerGUI_options") && 
				!(player.get("playerGUI_ID").equals(serverGUI_ID)))
		{
			Log.info("Client GUI", "Giving the player options from the server");
			StringArray options = (StringArray) player.get("playerGUI_options");
			stage.addActor(this.serverGUI_layout);
			this.serverGUI.setItems(options.data);
			this.serverGUI_layout.layout();
			this.serverGUI_layout.setPosition(this.stage.getWidth() / 2 - 
					this.serverGUI.getWidth() / 2, 
					this.stage.getHeight() / 2 - 100);
			this.serverGUI_ID = (Long) player.get("playerGUI_ID");
		}
		
		int tileSize = Constants.tileSize;
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
		}
		for (int i = 0; i < apFraction * 4; i++) {
			float baseFraction = i/4f;
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

		if (shouldScrollToChatBottom) {
			chatScrollPane.setScrollPercentY(100);
			shouldScrollToChatBottom = false;
		}

		ChatNetworkObject chat = null;
		// load chat lines and set them
		for (NetworkObject obj : gameState.getAllChildren().values()) {
			if (obj instanceof ChatNetworkObject) {
				chat = (ChatNetworkObject) obj;
				break;
			}
		}
		String[] chatLines = chat.getChatLines();
		SnapshotArray<Actor> children = chatInnerTable.getChildren();
		boolean differentLineNumber = children.size != chatLines.length;
		if (chatLines.length != 0 && (differentLineNumber
				|| !((Label) children.first()).getText().toString().equals(chatLines[0]))){
			System.out.println("Redrawing chat");
			chatInnerTable.clearChildren();
			for (String line : chat.getChatLines()) {
				chatInnerTable.row();
				Label label = new Label(line, Assets.skin);
				label.setAlignment(Align.left);
				label.setWrap(true);
				chatInnerTable.add(label).left().width(chatPanelWidth);
			}
			chatInnerTable.invalidateHierarchy();
			shouldScrollToChatBottom = true;
		}

		drawItems(player, animationTime);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	public void drawItems(Player player, float animationTime) {
		int tileSize = Constants.tileSize;
		if (player != null) {
			batch.begin();
			for (int i = 0; i < Constants.slots.length; i++) {
				if ((int) player.get("selection") == i) {
					batch.draw(selectedSlot.getKeyFrame(animationTime), (6 + i) * tileSize, tileSize);
				} else {
					batch.draw(slot.getKeyFrame(animationTime), (6 + i) * tileSize, tileSize);
				}
			}
			batch.end();
			batch.begin();
			for (int i = 0; i < Constants.slots.length; i++) {
				String itemType = (String) player.get(Constants.slots[i]);
				if (itemType != null) {
					TextureRegion tex = Assets.animations.get(itemType).getKeyFrame(animationTime);
					batch.draw(tex, (6 + i) * tileSize, tileSize);
				}
			}
			String weapon = (String) player.get("weapon");
			String spell = (String) player.get("spell");
			if (weapon != null) 
				batch.draw(Assets.animations.get(weapon).getKeyFrame(animationTime), 6 * tileSize, tileSize);
			if (spell != null) 
				batch.draw(Assets.animations.get(spell).getKeyFrame(animationTime), 7 * tileSize, tileSize);
			batch.end();
		}
	}

	public void handleResize(int width, int height) {
		viewport.update(width, height, true);
		stage.getViewport().update(width, height, true);
	}

	public String getChatMessage() {
		return chatField.getText();
	}

	public void clearChatField() {
		chatField.setText("");
	}

	public void toggleChatFocus() {
		if (stage.getKeyboardFocus() == chatField) {
			stage.setKeyboardFocus(null);
		} else {
			stage.setKeyboardFocus(chatField);
		}
	}

	public void renderPlayerMessage(String message) {
		playerMessage.setText(message);
		playerMessage.setPosition(stage.getWidth() / 2 - playerMessage.getWidth() / 2, 
				stage.getHeight() / 2 - playerMessage.getHeight() / 2);
		playerMessage.layout();
	}
	
	public void renderServerGUI(String options) {
		
	}
	
	public boolean isChatFocused() {
		return stage.getKeyboardFocus() == chatField;
	}
}
