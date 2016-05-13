package engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import objects.Player;

import java.util.ArrayList;
import java.util.List;

public class AudioManager {

    private Music bgm;
    private List<Sound> sounds;

    public AudioManager() {
        // Set up music
        bgm = Gdx.audio.newMusic(Gdx.files.internal(Config.ASSETS_PATH + "bgm/pokemon_center_rs.mp3"));
        bgm.setLooping(true);

        // Set up sounds
        sounds = new ArrayList<>();
        for (SFX sfx : SFX.values()) {
            sounds.add(Gdx.audio.newSound(Gdx.files.internal(Config.ASSETS_PATH + "sfx/" + sfx.name().toLowerCase() + ".wav")));
        }
    }

    public void playSound(SFX type) {
        sounds.get(type.ordinal()).play();
    }

    public void playSound(SFX type, Player p1, Player p2) {
        double dist = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        if (dist <= Math.max(Config.Camera.VIEWPORT_WIDTH, Config.Camera.VIEWPORT_HEIGHT)) {
            playSound(type);
        }
    }

    public Music getBgm() {
        return bgm;
    }

    public enum SFX {
        FIRE_ATTACK,
        FIRE_HIT,
        WATER_ATTACK,
        WATER_HIT,
        GRASS_ATTACK,
        GRASS_HIT,
        FAINT,
        CHAT,
        WALL_HOP,
        WALL_BUMP,
        CONNECT,
        DISCONNECT
    }

}
