package engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import engine.structs.UserList;
import networking.ClientThread;
import objects.Entity;
import objects.PlayerOnline;
import networking.ServerThread;
import networking.packets.Packet00Login;
import networking.packets.Packet01Disconnect;
import objects.Player;


public class Core extends Game {

    // Engine variables/constants
    private static final float UPDATE_RATE = 1/15f;
    private static final float ANIM_RATE = 1/2f;
    private static float delta = 0;
    private static float animDelta = 0;
    public boolean playMode = false;
    public boolean isHost = false;

    // Object classes
    private UserList<Player> players; // <------ Make this static ?

    // Engine classes
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture tex;
    Sprite sprite;
    AssetManager assets;
    Logic logic;
    KeyboardProcessor input;
    UI ui;
    InputMultiplexer multiplexer;

    // Networking classes
    ServerThread server;
    ClientThread client;

	@Override
	public void create () {
        // Object-related
        players = new UserList<Player>();

        // Engine
        assets = new AssetManager();
        cam = new OrthographicCamera(480, 320);
        ui = new UI(this); // Note: Networking commences here, playMode = true

        // Input handling
        multiplexer = new InputMultiplexer();
        input = new KeyboardProcessor(this);
        multiplexer.addProcessor(ui.getStage());
        multiplexer.addProcessor(input);
        Gdx.input.setInputProcessor(multiplexer);

        // Overworld data
        batch = new SpriteBatch();
		tex = AssetManager.level;
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Player spawn position
        sprite =  new Sprite(tex);
	    sprite.setOrigin(0,0);
        sprite.setPosition(-864,-550);
    }

    @Override
    public void dispose(){
        // Send disconnect packet to server
        closeNetworking();
        batch.dispose();
        tex.dispose();
        ui.dispose();
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update logic 15 times per second
        if (delta > UPDATE_RATE) {
            if(playMode) {
                logic.update(players.getMainPlayer());
            }
            delta -= UPDATE_RATE;
        }

        // Update animations 2 times per second
        if(animDelta > ANIM_RATE){
            animDelta -= ANIM_RATE;
        }

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
		sprite.draw(batch);
        if(playMode) {
            for(Player p : getPlayers()) {
                p.render(animDelta, batch);
            }
        }
		batch.end();

        ui.render();

        // DEBUG STUFF
//        if(playMode) {
//            ShapeRenderer sr = new ShapeRenderer();
//            sr.begin(ShapeRenderer.ShapeType.Line);
//            sr.setProjectionMatrix(cam.combined);
//            sr.setColor(Color.BLACK);
//            sr.rect(mainPlayer.getCollisionX(), mainPlayer.getCollisionY(), Config.CHAR_WIDTH, Config.CHAR_HEIGHT);
//            sr.rect(mainPlayer.getNameX(),
//                    mainPlayer.getNameY(),
//                    AssetManager.font.getBounds(mainPlayer.getUsername()).width,
//                    AssetManager.font.getBounds(mainPlayer.getUsername()).height);
//            sr.end();
//        }

        delta += Gdx.graphics.getDeltaTime();
        animDelta += Gdx.graphics.getDeltaTime();
	}

    public synchronized UserList<Player> getPlayers() {
        return this.players;
    }

    public void startNetworking(String ip) {
        if(isHost){
            System.out.println("Running as server.");
            server = new ServerThread(this);
            server.start();
        }
        System.out.println("Running as client.");
        client = new ClientThread(this, ip);
        client.start();
    }

    public void initMainPlayer(String username) {
        PlayerOnline p = new PlayerOnline(username);
        players.setMainPlayer(p); // <----- forced addition
        players.add(p.getUID(), p); // <----------------- Add to BOTH
        Packet00Login loginPacket = new Packet00Login(p.getUID(), p.getUsername(),
                p.getX(), p.getY(), p.getDirection().getNum(), p.getType().getNum());
        if(server != null) {
            server.addConnection(p, loginPacket);
        }
        loginPacket.writeDataFrom(client);
        logic = new Logic(this, cam);
        playMode = true;
    }

    /* Update values and animation frame */
    public void updatePlayer(long uid, String username, float x, float y, boolean isMoving, Entity.Direction dir, Player.Type type) {
        /* Player is not always added to players list ??? */
        Player p = getPlayers().get(uid);
        if(p != null) {
            p.setX(x);
            p.setY(y);
            p.setMoving(isMoving);
            p.setDirection(dir);
            p.setType(type);
            if (isMoving) {
                p.getAnim().play();
            }
        } else {
            System.err.println("Error: User not found - " + p.getUsername());
            /* Attempt to add to players list again */
            getPlayers().add(uid, new PlayerOnline(uid, x, y, dir, type, false, username, null, -1));
        }
    }

    /**
     * Attempts to send a packet to server indicating this
     * client wants to disconnect.
     */
    private void closeNetworking() {
        new Packet01Disconnect(getPlayers().getMainPlayer().getUID()).writeDataFrom(client);
    }
}
