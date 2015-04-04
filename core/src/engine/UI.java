package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UI {

    Core core;
    Skin skin;
    Stage stage;

    // Temporary input management ideas
    String input = "";
    boolean isHost = false;

    public UI(Core core) {
        this.core = core;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);

        runSetup();
    }

    public void render() {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void dispose(){
        stage.dispose();
        skin.dispose();
    }

    public void runSetup(){
        // Become host dialog
        Dialog d1 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                isHost = object.toString().equals("true");
                System.out.println("Is host: " + isHost);

                requestUsername();
            }
        };
        d1.setMovable(false);
        d1.text("Host a server?").button("Yes", true).button("No", false).key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false).show(stage);
    }

    public void requestUsername() {
        // Request username dialog
        final TextField field = new TextField("", skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                updateInput(field.getText());
                core.setPlayMode(true);
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.add(field).minWidth(300);
        d2.text("Choose a username!").key(Input.Keys.ENTER, null).show(stage);

        stage.setKeyboardFocus(field);
    }

    private void updateInput(String input){
        this.input = input;
        System.out.println("Username: " + input);
    }
}
