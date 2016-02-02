package networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import engine.ClientCore;
import engine.Config;
import engine.UI;
import engine.structs.Score;
import engine.structs.ScoreSet;
import objects.BasePlayer;

import java.util.List;

public class Scoreboard {

    private ClientCore clientCore;
    private UI ui;
    // Part of UI
    private TextArea scoreboard;

    public Scoreboard(ClientCore clientCore) {
        this.clientCore = clientCore;
        this.ui = clientCore.getUI();

        initScoreboard();
    }

    /* Initialize Chat UI */
    private void initScoreboard() {
        final float width = 130;
        final float height = 150;
        float x = Gdx.graphics.getWidth() - width;
        float y = Gdx.graphics.getHeight() - height;

        // Text Area (Always on)
        scoreboard = new TextArea("", ui.getSkin(), "chat-faded");
        scoreboard.setX(x);
        scoreboard.setY(y);
        scoreboard.setWidth(width);
        scoreboard.setHeight(height);
        scoreboard.setPrefRows(Config.Engine.MAX_SCORE_ROWS);
        scoreboard.setDisabled(true);
        scoreboard.setVisible(true);
        scoreboard.setTouchable(Touchable.disabled);

        ui.getStage().addActor(scoreboard);
    }

    public void updateScoreboardUI(ScoreSet scores) {
        String scoreText = "";
        int count = 1;
        List<Score> sorted = scores.sorted();

        // Generate string of high scores
        for (int i = sorted.size() - 1; i >= 0; i--) {
            Score s = sorted.get(i);
            BasePlayer p = clientCore.getPlayers().get(s.uid);
            if (p != null) {
                scoreText += String.format(
                        "%1$-" + 14 + "s%2$" + 6 + "s\n",
                        count + ". " + p.getUsername(),
                        s.score);
                count++;
            }

            if (count > Config.Engine.MAX_SCORE_ROWS)
                break;
        }

        // Update text
        scoreboard.setText(scoreText);
    }
}
