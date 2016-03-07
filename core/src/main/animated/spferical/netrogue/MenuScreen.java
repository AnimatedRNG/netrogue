package animated.spferical.netrogue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Handles the main menu.
 *
 * @author matthew
 */
public class MenuScreen implements Screen {

    Stage stage;
    Table table;
    Label fpsLabel;

    public MenuScreen() {
        create();
    }

    /**
     * Sets everything up.
     */
    public void create() {

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        TextButton playButton = new TextButton("Play", Assets.skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            Game game = (Game) Gdx.app.getApplicationListener();
            game.setScreen(new GameScreen());
            }
        });

        table = new Table();
        table.row();

        table.layout();
        fpsLabel = new Label("fps:", Assets.skin);

        Label titleLabel = new Label("NetRogue", Assets.skin);

        table.add(titleLabel);
        table.defaults().spaceBottom(10);
        table.row();
        table.add(playButton).center().fillX().expandY();
        table.row();
        table.add(fpsLabel).bottom();
        table.pack();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.setSize(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
