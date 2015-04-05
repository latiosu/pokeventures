package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class UI {

    private Core core;
    private Skin skin;
    private Stage stage;
    private String text = ""; /* Possibly used for keyboard input */
    private boolean hasFocus = false;

    public UI(Core core) {
        this.core = core;
        skin = AssetManager.skin;
        stage = new Stage(new ScreenViewport());

        runSetup();
    }

    public void render() {
        // Render UI elements
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
                // Determine if hosting server
                if(core.isHost = object.toString().equals("true")) {
                    core.startNetworking("localhost");
                    requestUsername();
                } else {
                    requestServer();
                }
            }
        };
        d1.setMovable(false);
        d1.getContentTable().pad(10, 100, 0, 100);
        d1.getButtonTable().pad(0, 0, 20, 0);
        d1.text("Host a server?").button("Yes", true).button("No", false).key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false).show(stage);
    }

    /* Requests server IP if isHost is true */
    public void requestServer() {
        // Request server IP dialog
        final TextField field = new TextField(Config.SERVER_IP, skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);
        field.setCursorPosition(field.getText().length());

        Dialog d2 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                setText(field.getText());
                core.startNetworking(text); // Start networking connections
                requestUsername();
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.add(field).minWidth(300);
        d2.text("Enter Server IP:").key(Input.Keys.ENTER, null).show(stage);

        this.hasFocus = true;
        stage.setKeyboardFocus(field);
    }

    /* Initializes main player */
    public void requestUsername() {
        // Request username dialog
        final TextField field = new TextField("", skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("Setup", skin, "dialog") {
            protected void result (Object object) {
                setText(field.getText()); // <----- Set to lowercase
                core.initMainPlayer(sanitizeText(text)); // Define main player for client
                hasFocus = false;
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.add(field).minWidth(300);
        d2.text("Choose a username!").key(Input.Keys.ENTER, null).show(stage);

        this.hasFocus = true;
        stage.setKeyboardFocus(field);
    }

    private String sanitizeText(String input) {
        String regex = "[^a-z]+";
        return input.toLowerCase().replaceAll(regex, "");
    }

    private void setText(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean hasFocus() {
        return hasFocus;
    }
}
