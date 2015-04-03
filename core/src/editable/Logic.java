package editable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import engine.*;

/**
 * Created by latios on 26/03/15.
 */
public class Logic {

    InputHandler input;
    OrthographicCamera cam;
    Player player;
    PlayerAnimation anim;

    float mapWidth, mapHeight, maxCamX, maxCamY;

    public Logic(InputHandler i, Player p, OrthographicCamera cam, PlayerAnimation anim){
        input = i;
        player = p;
        this.cam = cam;
        this.anim = anim;

        mapWidth = 1280f;
        mapHeight = 1310f;
        maxCamX = (mapWidth/2f - cam.viewportWidth);
        maxCamY = (mapHeight/2f - cam.viewportHeight);
    }

    // Add any game logic you'd like here
    public void update(){

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
//            System.out.println("left");
            player.move(Direction.LEFT);

        } else if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            System.out.println("down");
            player.move(Direction.DOWN);

        } else if(Gdx.input.isKeyPressed(Input.Keys.UP)){
//            System.out.println("up");
            player.move(Direction.UP);

        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
//            System.out.println("right");
            player.move(Direction.RIGHT);

        }

        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
            player.changePlayer(Type.CHARMANDER);

        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
            player.changePlayer(Type.BULBASAUR);

        } else if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
            player.changePlayer(Type.SQUIRTLE);

        }

    }

}
