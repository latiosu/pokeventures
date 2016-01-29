package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import networking.ChatClient;

public class UI {

    private Core core;
    private Skin skin;
    private Stage stage;
    private String text = ""; /* Possibly used for keyboard input */
    private boolean hasFocus = false;
    private ChatClient cc;
    private Image setupBG;

    public UI(Core core) {
        this.core = core;
        skin = AssetManager.skin;
        stage = new Stage(new ScreenViewport());
        runSetup();
        initLabels();
    }

    public void render() {
        // Render UI elements
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void initLabels() {
        Label versionNumber = new Label(Config.VERSION, skin, "default");
        versionNumber.setName("version");
        versionNumber.setPosition((Config.VIEWPORT_WIDTH * 2f) - versionNumber.getWidth() - 10, 5);
        stage.addActor(versionNumber);
    }

    /* Contains a start networking call */
    private void runSetup() {
        // Render setup background
        setupBG = new Image(AssetManager.setupBG);
        setupBG.setName("setup-bg");
        stage.addActor(setupBG);

        // Become host dialog
        Dialog d1 = new Dialog("", skin, "dialog") {
            protected void result(Object object) {
                // Determine if hosting server
                if (core.isHost = object.toString().equals("true")) {
                    core.startNetworking("localhost");
                    requestUsername();
                } else {
                    requestServer();
                }
            }
        };
        d1.setMovable(false);
        d1.getContentTable().pad(10, 60, 0, 60);
        d1.getButtonTable().pad(0, 0, 20, 0);
        d1.text("Hosting a game or joining?").button("Host", true).button("Join", false).key(Input.Keys.ENTER, true)
                .key(Input.Keys.ESCAPE, false).show(stage);
    }

    /* Requests server IP if isHost is true */
    public void requestServer() {
        // Request server IP dialog
        final TextField field = new TextField(Config.SERVER_IP, skin, "plain");
        field.setMaxLength(15);
        field.setWidth(150);
        field.setAlignment(Align.center);
        field.setSelection(0, field.getText().length());

        Dialog d2 = new Dialog("", skin, "dialog") {
            protected void result(Object object) {
                setText(field.getText());
                core.startNetworking(text); // Start networking connections
                requestUsername();
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.pad(10, 60, 10, 60);
        d2.add(field);
        d2.text("Enter Server IP:").key(Input.Keys.ENTER, null).show(stage);

        setFocus(true);
        stage.setKeyboardFocus(field);
    }

    /* Initializes main player and game UI */
    private void requestUsername() {
        // Request username dialog
        final TextField field = new TextField("", skin, "plain");
        field.setMaxLength(15);
        field.setWidth(150);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("", skin, "dialog") {
            protected void result(Object object) {
                setText(field.getText());
                core.initMainPlayer(sanitizeText(text)); // Define main player for client
                initChat(); // Instantiate Chat client
                setFocus(false);

                setupBG.addAction(Actions.fadeOut(0.4f));
            }
        };
        d2.setMovable(false);
        d2.row();
        d2.pad(10, 60, 10, 60);
        d2.add(field);
        d2.text("Choose a name!").key(Input.Keys.ENTER, null).show(stage);

        setFocus(true);
        stage.setKeyboardFocus(field);
    }

    public void triggerRespawnScreen() {
        // Dim background
        setupBG = new Image(AssetManager.setupBG);
        setupBG.setName("setup-bg");
        stage.addActor(setupBG);

        int count = 5;
        // Show countdown timer to respawn
        Label countDown = new Label(Integer.toString(count), skin, "default");
        countDown.setFontScale(5, 5);
        countDown.setName("countdown");
        countDown.setPosition(Config.VIEWPORT_WIDTH - (2 * countDown.getWidth()), Config.VIEWPORT_HEIGHT * (5f / 4f));
        stage.addActor(countDown);

        long delta = 0;
        long lastTime = System.nanoTime();
//        while (count > 0) {
//            if (delta >= 1e9) {
//                count -= 1;
//                countDown.setText(Integer.toString(count));
//            }
//            delta += System.nanoTime() - lastTime;
//            lastTime = System.nanoTime();
//        }
    }

    public ChatClient getChatClient() {
        return cc;
    }

    private void initChat() {
        cc = new ChatClient(core); // Instantiate Chat client
    }

    private String sanitizeText(String input) {
        String regex = "[^a-z ]+";
        return input.toLowerCase().replaceAll(regex, "");
    }

    private void setText(String text) {
        this.text = text;
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
