package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import engine.structs.Message;
import engine.structs.TimeComparator;
import networking.ChatClient;

import java.util.PriorityQueue;
import java.util.Queue;

public class UI {

    private Core core;
    private Skin skin;
    private Stage stage;
    private String text = ""; /* Possibly used for keyboard input */
    private boolean hasFocus = false;
    private ChatClient cc;

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
        Dialog d1 = new Dialog("", skin, "dialog") {
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
        d1.text("Host a server?").button(" Yes ", true).button(" No ", false).key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false).show(stage).key(Input.Keys.N, false).key(Input.Keys.Y, true);
    }

    /* Requests server IP if isHost is true */
    public void requestServer() {
        // Request server IP dialog
        final TextField field = new TextField(Config.SERVER_IP, skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);
        field.setSelection(0, field.getText().length());

        Dialog d2 = new Dialog("", skin, "dialog") {
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

        setFocus(true);
        stage.setKeyboardFocus(field);
    }

    /* Initializes main player and game UI */
    public void requestUsername() {
        // Request username dialog
        final TextField field = new TextField("", skin);
        field.setMaxLength(15);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("", skin, "dialog") {
            protected void result (Object object) {
                setText(field.getText());
                core.initMainPlayer(sanitizeText(text)); // Define main player for client
                initChat(); // Instantiate Chat client
                setFocus(false);
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.add(field).minWidth(300);
        d2.text("What's your name?").key(Input.Keys.ENTER, null).show(stage);

        setFocus(true);
        stage.setKeyboardFocus(field);
    }

    public ChatClient getChatClient() {
        return cc;
    }

    private void initChat() {
        cc = new ChatClient(core); // Instantiate Chat client
    }

    private String sanitizeText(String input) {
        String regex = "[^a-z' ']+";
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

    public Skin getSkin() {
        return skin;
    }

    public void setFocus(boolean b) {
        this.hasFocus = b;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public void showChat(boolean b) {
        cc.showChat(b);
    }
}
