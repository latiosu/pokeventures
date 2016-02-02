package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import engine.structs.Event;
import engine.structs.Number;
import networking.ChatClient;
import networking.Scoreboard;
import objects.Player;
import objects.structs.Direction;
import objects.structs.State;

public class UI {

    ClientCore clientCore;
    private Skin skin;
    private Stage stage;
    private String text = ""; /* Possibly used for keyboard input */
    private boolean hasFocus = false;
    private Scoreboard sb;
    private Image setupBG;

    public UI(ClientCore clientCore) {
        this.clientCore = clientCore;
        skin = AssetManager.skin;
        stage = new Stage(new ScreenViewport());
        runSetup();
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

    private void runSetup() {
        // Render setup background
        setupBG = new Image(AssetManager.setupBG);
        setupBG.setName("setup-bg");
        stage.addActor(setupBG);

        // Request username dialog
        final TextField field = new TextField("", skin, "plain");
        field.setMaxLength(Config.Engine.MAX_USERNAME_LENGTH);
        field.setWidth(150);
        field.setAlignment(Align.center);

        Dialog d2 = new Dialog("", skin, "dialog") {
            protected void result(Object object) {
                setText(field.getText());
                sb = new Scoreboard(clientCore); // Instantiate Scoreboard
                clientCore.initMainPlayer(sanitizeText(text)); // Define main player for client
                setFocus(false);

                setupBG.addAction(Actions.fadeOut(0.4f));

                // Show labels after setup
                if (Config.DEBUG) {
                    // Version label
                    Label versionNumber = new Label(Config.Engine.VERSION, skin, "default");
                    versionNumber.setName("version");
                    versionNumber.setPosition((Config.Camera.VIEWPORT_WIDTH * 2f) - versionNumber.getWidth() - 10, 5);
                    stage.addActor(versionNumber);
                }
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

        Number count = new Number(5);
        // Show countdown timer to respawn
        Label countDown = new Label(count.toString(), skin, "default");
        countDown.setAlignment(Align.center);
        countDown.setFontScale(5, 5);
        countDown.setName("countdown");
        countDown.setPosition(Config.Camera.VIEWPORT_WIDTH - (countDown.getWidth()), Config.Camera.VIEWPORT_HEIGHT * (5f / 4f));
        stage.addActor(countDown);

        // Confirm respawn screen
        clientCore.addEvent(new Event(5, task -> {
            count.add(-1);
            countDown.setText(count.toString());
            if (count.getValue() == 0) {
                countDown.setText("Press SPACE to revive!");
                clientCore.addEvent(new Event(confirm -> {
                    if (UserInputProcessor.attackKeys[1]) {
                        // Clear overlay
                        countDown.remove();
                        setupBG.addAction(Actions.fadeOut(0.4f));

                        // Trigger respawn
                        Player mp = clientCore.getPlayers().getMainPlayer();
                        mp.setHp(mp.getMaxHp());
                        mp.setX(Config.World.SPAWN_X);
                        mp.setY(Config.World.SPAWN_Y);
                        mp.setAlive(true);
                        mp.setDirection(Direction.DOWN);
                        mp.setState(State.IDLE);
                        mp.setScore(0);

                        // Ignore initial attack
                        UserInputProcessor.attackKeys[1] = false;
                        return true;
                    }
                    return false;
                }));
                return true;
            }
            return false;
        }));
    }

    public Scoreboard getScoreboard() {
        return sb;
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

}
