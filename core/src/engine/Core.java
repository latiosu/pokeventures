package engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import engine.structs.AttackList;
import engine.structs.Message;
import engine.structs.UserList;
import networking.ClientThread;
import networking.ServerThread;
import networking.packets.Packet00Login;
import networking.packets.Packet01Disconnect;
import networking.packets.Packet03Chat;
import objects.*;
import objects.Tiles.Tile;
import objects.attacks.Attack;
import objects.attacks.AttackType;
import objects.attacks.RangedAttack;


public class Core extends Game {

    // Engine variables/constants
    private static float delta = 0;

    public boolean playMode = false;
    public boolean isHost = false;

    // Object classes
    private UserList players;
    private AttackList attacks;

    // Rendering classes
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture tex;
    private Sprite sprite;

    // Engine classes
    private WorldManager world;
    private Logic logic;
    private UI ui;

    // Networking classes
    private ServerThread server;
    private ClientThread client;

    @Override
    public void create() {
        // Object-related
        players = new UserList();
        attacks = new AttackList();

        // Engine
        new AssetManager();
        cam = new OrthographicCamera(Config.VIEWPORT_WIDTH, Config.VIEWPORT_HEIGHT);
        ui = new UI(this);
        world = new WorldManager();


        // Input handling
        InputMultiplexer multiplexer = new InputMultiplexer();
        UserInputProcessor input = new UserInputProcessor(this);
        multiplexer.addProcessor(ui.getStage());
        multiplexer.addProcessor(input);
        Gdx.input.setInputProcessor(multiplexer);

        // Overworld data
        batch = new SpriteBatch();
        tex = world.loadWorld(Config.MAP);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        sprite = new Sprite(tex);
        sprite.setOrigin(0, 0);

        // Player spawn position
        cam.position.x = Config.SPAWN_X;
        cam.position.y = Config.SPAWN_Y;
        cam.update();
    }

    @Override
    public void dispose() {
        // Send disconnect packet to server
        closeNetworking();
        batch.dispose();
        tex.dispose();
        ui.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Update logic 15 times per second
        if (delta > Config.UPDATE_RATE) {
            if (playMode) {
                logic.update(players.getMainPlayer());
            }
            delta -= Config.UPDATE_RATE;
        }

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        sprite.draw(batch);
        if (playMode) {
            for (Player p : players) {
                p.render(Gdx.graphics.getDeltaTime(), batch);
            }
            for (Attack a : attacks) {
                a.render(Gdx.graphics.getDeltaTime(), batch);
            }
        }
        batch.end();

        ui.render();

        // DEBUG STUFF
        if (Config.DEBUG) {
            if (playMode) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                PlayerOnline mp = players.getMainPlayer();
                ShapeRenderer sr = new ShapeRenderer();
                sr.setAutoShapeType(true);
                sr.setProjectionMatrix(cam.combined);
                sr.begin(ShapeRenderer.ShapeType.Filled); // BEGIN DEBUG RENDERING

                // Render blocked as red
                sr.setColor(1f, 0f, 0f, 0.3f);
                for (Tile t : world.getBlocked()) {
                    sr.rect(t.getX(), t.getY(), 16, 16);
                }

                // Render player centre tile as white
                Tile a = world.getTile(mp.getX(), mp.getY());
                sr.setColor(1f, 1f, 1f, 0.4f);
                sr.rect(a.getX(), a.getY(), 16, 16);

                // Render player outline as yellow
                sr.set(ShapeRenderer.ShapeType.Line);
                sr.setColor(Color.YELLOW);
                sr.rect(mp.getX(), mp.getY(), Config.CHAR_COLL_WIDTH, Config.CHAR_COLL_HEIGHT);

                sr.end(); // END DEBUG RENDERING
            }
        }

        delta += Gdx.graphics.getDeltaTime();
    }

    public void startNetworking(String ip) {
        if (isHost) {
            System.out.println("Running as server.");
            server = new ServerThread();
            server.start();
        }
        System.out.println("Running as client.");
        client = new ClientThread(this, ip);
        client.start();
    }

    public void initMainPlayer(String username) {
        PlayerOnline p = new PlayerOnline(username);
        players.setMainPlayer(p);
        players.add(p.getUID(), p);
        Packet00Login loginPacket = new Packet00Login(p.getUID(), p.getUsername(),
                p.getX(), p.getY(), p.getDirection().getNum(), p.getType().getNum());
        if (server != null) {
            server.addConnection(p, loginPacket);
        }
        // Send packet to server to sync main player
        loginPacket.writeDataFrom(client);
        logic = new Logic(this, cam);
        playMode = true;
    }

    /* Update values and animation frame */
    public void updatePlayer(long uid, String username, float x, float y, State state,
                             Direction dir, PlayerType type) {
        Player p = getPlayers().get(uid);
        if (p != null) {
            p.setX(x);
            p.setY(y);
            p.setState(state);
            p.setDirection(dir);
            p.setType(type);
            p.getAnim().updateAnim();
        } else {
            System.err.println("Error: User not found - " + username);
        }
    }

    public void updateAttack(long id, long uid, PlayerType ptype, Direction dir, float x, float y, AttackType type, boolean isAlive) {
        Attack atk = getAttacks().get(id);
        if (atk != null) {
            if (!atk.isAlive()) { // Remove if not alive
                getAttacks().remove(id);
            } else {
                atk.setDirection(dir);
                atk.setX(x);
                atk.setY(y);
                atk.setAlive(isAlive);
            }
        } else {
            switch (type) {
                case RANGED:
                    atk = new RangedAttack(id, uid, ptype, dir, x, y);
                    break;
            }
            attacks.add(id, atk);
        }
    }

    /**
     * Attempts to send a packet to server indicating the
     * client wants to disconnect.
     */
    private void closeNetworking() {
        new Packet01Disconnect(getPlayers().getMainPlayer().getUID()).writeDataFrom(client);
    }

    /**
     * Attempts to send a packet to server to synchronize
     * new chat message.
     */
    public void registerMsg(Packet03Chat packet) {
        packet.writeDataFrom(client);
    }

    /**
     * Stores message inside chat client and updates ui components.
     * Note: DOES NOT COMMUNICATE WITH SERVER.
     */
    public void storeMsg(Message msg) {
        ui.getChatClient().storeMsg(msg);
    }

    public UI getUI() {
        return ui;
    }

    public ClientThread getClientThread() {
        return client;
    }

    public WorldManager getWorldManager() {
        return world;
    }

    public synchronized UserList getPlayers() {
        return this.players;
    }

    public synchronized AttackList getAttacks() {
        return this.attacks;
    }
}
