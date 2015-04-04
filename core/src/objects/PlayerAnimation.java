package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by latios on 26/03/15.
 */
public class PlayerAnimation {

    private int one, two, three = 0;

    private final int CHAR_FRAMES = 8;
    private final int ANIM_TYPES = 4;
    private final float ANIM_RATE = 1/5f;

    private Type currentType;
    private Animation currentAnimation;
    private boolean playing;

    private Animation[] charmander, bulbasaur, squirtle;

    public PlayerAnimation(Type t) {
        currentType = t;
        playing = false;

        charmander = new Animation[ANIM_TYPES];
        bulbasaur = new Animation[ANIM_TYPES];
        squirtle = new Animation[ANIM_TYPES];

        create(charmander, new TextureAtlas(Gdx.files.internal("charmander.atlas")));
        create(bulbasaur, new TextureAtlas(Gdx.files.internal("bulbasaur.atlas")));
        create(squirtle, new TextureAtlas(Gdx.files.internal("squirtle.atlas")));

        changeType(currentType);
    }

    private void create(Animation[] anim, TextureAtlas atlas){
        // Retrieve textures
        TextureRegion[] frames = new TextureRegion[CHAR_FRAMES];
        for(int i=1; i<=CHAR_FRAMES; i++) {
            frames[i - 1] = atlas.findRegion("" + i);
        }

        // Generate Animations (down, left, up, right)
        for(int i=0; i<ANIM_TYPES; i++){
            anim[i] = new Animation(ANIM_RATE, frames[i*2], frames[i*2+1]);
        }
    }

    public void changeType(Type t){
        currentType = t;
        switch (t){
            case CHARMANDER:
                currentAnimation = charmander[0];
                break;
            case BULBASAUR:
                currentAnimation = bulbasaur[0];
                break;
            case SQUIRTLE:
                currentAnimation = squirtle[0];
                break;
        }
    }

    public void play(Type t, Direction d){
        playing = true;

        if(t == Type.CHARMANDER) {
            switch (d){
                case DOWN:
                    currentAnimation = charmander[0];
                    break;
                case LEFT:
                    currentAnimation = charmander[1];
                    break;
                case UP:
                    currentAnimation = charmander[2];
                    break;
                case RIGHT:
                    currentAnimation = charmander[3];
                    break;
            }
        } else if (t == Type.BULBASAUR) {
            switch (d){
                case DOWN:
                    currentAnimation = bulbasaur[0];
                    break;
                case LEFT:
                    currentAnimation = bulbasaur[1];
                    break;
                case UP:
                    currentAnimation = bulbasaur[2];
                    break;
                case RIGHT:
                    currentAnimation = bulbasaur[3];
                    break;
            }
        } else if (t == Type.SQUIRTLE) {
            switch (d){
                case DOWN:
                    currentAnimation = squirtle[0];
                    break;
                case LEFT:
                    currentAnimation = squirtle[1];
                    break;
                case UP:
                    currentAnimation = squirtle[2];
                    break;
                case RIGHT:
                    currentAnimation = squirtle[3];
                    break;
            }
        }
    }

    public void stop(){
        playing = false;
    }

    public TextureRegion getFrame(float delta){
//        System.out.println(one + " - " + two + " - " + three);
        if(playing && !currentAnimation.isAnimationFinished(delta)){
//            one++;
            return currentAnimation.getKeyFrame(delta,false);
        } else {
//            two++;
            stop();
            return currentAnimation.getKeyFrame(1);
        }
    }

}
