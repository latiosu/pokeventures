package engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import editable.Logic;

public class Core extends Game {

    // Engine variables/constants
    private static final float UPDATE_RATE = 1/15f;
    private static final float ANIM_RATE = 1/2f;
    private static final Type DEFAULT_TYPE = Type.CHARMANDER;
    private static float delta = 0;
    private static float animDelta = 0;


    // Engine classes
    OrthographicCamera cam;
    SpriteBatch batch;
	Texture tex;
	Sprite sprite;
    Logic logic;
    InputHandler input;
    PlayerAnimation anim;
    Player player;

//    Animation test;

	@Override
	public void create () {
        cam = new OrthographicCamera(480, 320);
        input = new InputHandler();
        anim = new PlayerAnimation(DEFAULT_TYPE); // Charmander is default
        player = new Player(DEFAULT_TYPE, anim, cam);
        logic = new Logic(input, player, cam, anim);

        batch = new SpriteBatch();
		tex = new Texture(Gdx.files.internal("overworld.png"));
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        sprite =  new Sprite(tex);
	    sprite.setOrigin(0,0);
        sprite.setPosition(-864,-550);

//        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void dispose(){
        batch.dispose();
        tex.dispose();
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update logic 15 times per second
        if(delta > UPDATE_RATE){
            logic.update();
            delta -= UPDATE_RATE;
        }

        // Update animations 2 times per second
        if(animDelta > ANIM_RATE){
            animDelta -= ANIM_RATE;
        }

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
		sprite.draw(batch);
        batch.draw(anim.getFrame(animDelta), player.getX(), player.getY());
		batch.end();

        delta += Gdx.graphics.getDeltaTime();
        animDelta += Gdx.graphics.getDeltaTime();
	}

}
