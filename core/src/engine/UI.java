package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UI {

    private Core core;
    private Skin skin;
    private Stage stage;
    private String text = ""; /* Possibly used for keyboard input */

    public UI(Core core) {
        this.core = core;
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

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

    /* Contains a start networking call */
    public void runSetup(){
        // Become host dialog
        Dialog d1 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                core.isHost = object.toString().equals("true");  // Determine if hosting server
                core.startNetworking(); // Start sockets and network threads here
                requestUsername(); // Request username
            }
        };
        d1.setMovable(false);
        d1.getContentTable().pad(10, 100, 0, 100);
        d1.getButtonTable().pad(0, 0, 20, 0);
        d1.text("Host a server?").button("Yes", true).button("No", false).key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false).show(stage);
    }

    /* Initializes main player */
    public void requestUsername() {
        // Request username dialog
        final TextField field = new TextField("", skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                updateText(field.getText());
                core.initMainPlayer(text); // Define main player for client
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.add(field).minWidth(300);
        d2.text("Choose a username!").key(Input.Keys.ENTER, null).show(stage);

        stage.setKeyboardFocus(field);
    }

    private void updateText(String text){
        this.text = text;
        System.out.println("Username: " + text);
    }

    public String getText() {
        return text;
    }

    public Stage getStage() {
        return stage;
    }
}
