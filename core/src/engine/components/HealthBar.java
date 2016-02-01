package engine.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import engine.AssetManager;
import engine.Config;
import objects.Player;

public class HealthBar extends Actor {

    private Player parent;
    private NinePatchDrawable hpBarBackground;
    private NinePatchDrawable hpBar;

    public HealthBar(Player parent) {
        TextureAtlas skinAtlas = AssetManager.skin.getAtlas();
        NinePatch hpBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("textbox_plain"), 0, 0, 1, 1);
        NinePatch hpBarPatch = new NinePatch(skinAtlas.findRegion("healthbar_red"), 0, 0, 1, 1);
        hpBarBackground = new NinePatchDrawable(hpBarBackgroundPatch);
        hpBarBackground = hpBarBackground.tint(Color.WHITE);
        hpBar = new NinePatchDrawable(hpBarPatch);
        hpBar = hpBar.tint(Color.RED);
        this.parent = parent;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float progress = parent.getHp()/parent.getMaxHp();
        hpBarBackground.draw(batch, parent.getX() - 7, parent.getY() + Config.Character.CHAR_HEIGHT + 5, 24 , 1);
        hpBar.draw(batch, parent.getX() - 7, parent.getY() + Config.Character.CHAR_HEIGHT + 5, progress * 24 , 1);
    }
}
