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
import com.sun.corba.se.spi.activation.Server;
import engine.structs.AttackList;
import engine.structs.Event;
import engine.structs.Message;
import engine.structs.UserList;
import networking.packets.*;
import networking.threads.ClientThread;
import networking.threads.ServerThread;
import objects.*;
import objects.Tiles.Tile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


public class Core extends Game {

    // Engine variables/constants
    private float delta = 0;
    private float debugDelta = 0;
    private int frames = 0;
    private long bootTime = System.nanoTime();

    public boolean playMode = false;
    public boolean isHost = false;

    // Object classes
    private UserList players;
    private AttackList attacks;
    private List<Event> eventQueue;

    // Rendering classes
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture tex;
    private Sprite sprite;

    // Engine classes
    private WorldManager world;
    private UI ui;

    // Networking classes
    private ServerThread server;
    private ClientThread client;

    @Override
    public void create() {
        // Object-related
        players = new UserList();
        attacks = new AttackList();
        eventQueue = new CopyOnWriteArrayList<>();

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
        new Packet01Disconnect(getPlayers().getMainPlayer().getUID()).writeDataFrom(client);
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
                updateMainPlayer();
                updateAttackList();
                updateCamera();
            }
            processEvents(delta);
            delta -= Config.UPDATE_RATE;
        }

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        sprite.draw(batch);
        if (playMode) {
            // Reversed loop to render main player last
            for (int i = players.size() - 1; i >= 0; i--) {
                players.get(i).render(Gdx.graphics.getDeltaTime(), batch);
            }
            for (Attack a : attacks) {
                a.render(Gdx.graphics.getDeltaTime(), batch);
            }
        }
        batch.end();

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

                // Render blocked tile as red
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

                // Render projectile outline as red
                sr.setColor(1f, 0f, 0f, 0.3f);
                for (Attack atk : attacks) {
                    sr.rect(atk.getPosX(), atk.getPosY(), Config.TILE_SIZE, Config.TILE_SIZE);
                }

                sr.end(); // END DEBUG RENDERING

                // Debug Printing
                if (debugDelta >= Config.DEBUG_LOG_RATE) {
                    long currentTime = System.nanoTime() - bootTime;
                    String timeString = String.format("%02d:%02d:%02d",
                            TimeUnit.NANOSECONDS.toHours(currentTime),
                            TimeUnit.NANOSECONDS.toMinutes(currentTime)%60,
                            TimeUnit.NANOSECONDS.toSeconds(currentTime)%60);
                    System.out.printf("[%s] DEBUG: FPS=%d - Events=%d - Attacks=%d - Users=%d\n",
                            timeString, frames/3, eventQueue.size(), attacks.size(), players.size());
                    debugDelta -= Config.DEBUG_LOG_RATE;
                    frames = 0;
                }
                debugDelta += Gdx.graphics.getDeltaTime();
            }
        }

        // Render UI on top of everything else
        ui.render();

        frames += 1;
        delta += Gdx.graphics.getDeltaTime();
    }

    private void processEvents(float delta) {
        List<Event> finished = new CopyOnWriteArrayList<>();
        // Update queued events first
        for (Event e : eventQueue) {
            // Finished events
            if (e.update(delta)) {
                finished.add(e);
            }
        }
        // Clean up finished events
        for (Event e : finished) {
            eventQueue.remove(e);
        }
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
        playMode = true;
    }

    /* Update values and animation frame */
    public void handlePlayerPacket(long uid, String username, float x, float y, State state,
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

    public void handleAttackPacket(long id, long uid, Direction dir, float x, float y, boolean isAlive) {
        Attack atk = getAttacks().get(id);
        if (atk != null) {
            // Update attack if registered
            if (!atk.isAlive() || !isAlive) { // Remove if not alive
                getAttacks().remove(id);
            }
            // Else do nothing
        } else {
            // Store attack if not registered
            atk = new Attack(id, getPlayers().get(uid), dir, x, y);
            attacks.add(id, atk);
        }
    }

    private void updateCamera() {
        Player mp = getPlayers().getMainPlayer();

        cam.position.x = mp.getX();
        cam.position.y = mp.getY();
        cam.update();

        // This camera algorithm only follows player if camera can be centered.
        if (cam.position.x > Config.CAM_MAX_X) {
            cam.position.x = Config.CAM_MAX_X;
        }
        if (cam.position.y > Config.CAM_MAX_Y) {
            cam.position.y = Config.CAM_MAX_Y;
        }
        if (cam.position.x < Config.CAM_MIN_X) {
            cam.position.x = Config.CAM_MIN_X;
        }
        if (cam.position.y < Config.CAM_MIN_Y) {
            cam.position.y = Config.CAM_MIN_Y;
        }
        cam.update();
    }

    private void updateMainPlayer() {
        Player mp = getPlayers().getMainPlayer();

        // Trigger fainting and respawn
        if (!mp.isAlive()) {
            if (mp.getState() != State.FAINTED) {
                mp.setState(State.FAINTED);
                Packet02Move pk = new Packet02Move(mp.getUID(), mp.getUsername(),
                        mp.getX(), mp.getY(), mp.getState().getNum(),
                        mp.getDirection().getNum(), mp.getType().getNum());
                pk.writeDataFrom(getClientThread());

                ui.triggerRespawnScreen();
            }

            // Ignore player input
            return;
        }

        // Input logic
        boolean[] arrows = UserInputProcessor.directionKeys;
        boolean[] attacks = UserInputProcessor.attackKeys;
        mp.setType(UserInputProcessor.selectedType);

        // Update animation state
        if (!arrows[0] && !arrows[1] && !arrows[2] && !arrows[3]) {
            mp.setState(State.IDLE);
        } else {
            mp.setState(State.WALK);
        }

        // Attack / Movement logic
        if (attacks[1]) {
            if (Attack.canRangedAttack(mp)) {
                Attack atk = new Attack(mp);
                getAttacks().add(atk.getId(), atk);
                mp.updateLastAttackTime();
                Packet04Attack pk = new Packet04Attack(atk.getId(),  atk.getOwner().getUID(),
                        atk.getOwner().getType().getNum(), atk.getDirection().getNum(),
                        atk.getX(), atk.getY(), atk.isAliveNum());
                pk.writeDataFrom(client);
            }
        }
        if (mp.getState() == State.WALK) {
            // Update movement direction flags only during walk state
            if (arrows[0]) {
                mp.setDirection(Direction.DOWN);
            } else if (arrows[1]) {
                mp.setDirection(Direction.LEFT);
            } else if (arrows[2]) {
                mp.setDirection(Direction.UP);
            } else if (arrows[3]) {
                mp.setDirection(Direction.RIGHT);
            }
            switch (mp.getDirection()) {
                case DOWN:
                    if (mp.getY() > 0) {
                        mp.setY(mp.getY() - Config.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if (mp.getX() > 0) {
                        mp.setX(mp.getX() - Config.WALK_DIST);
                    }
                    break;
                case UP:
                    if (mp.getY() < AssetManager.level.getHeight() - Config.CHAR_COLL_HEIGHT) {
                        mp.setY(mp.getY() + Config.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if (mp.getX() < AssetManager.level.getWidth() - Config.CHAR_COLL_WIDTH) {
                        mp.setX(mp.getX() + Config.WALK_DIST);
                    }
                    break;
            }
        }

        // Update player animations immediately for responsiveness
        mp.getAnim().updateAnim();
        // <----- Play sounds immediately for responsiveness in the future

        // Handle collision for main player
        getWorldManager().handleCollision(mp);

        // Send movement packet with freshly updated main player data
        Packet02Move pk = new Packet02Move(mp.getUID(), mp.getUsername(),
                mp.getX(), mp.getY(), mp.getState().getNum(),
                mp.getDirection().getNum(), mp.getType().getNum());
        pk.writeDataFrom(getClientThread());
    }

    /**
     * Each client checks for collisions with their own character and all existing projectiles.
     * Note: *May need to optimize with quadtree*
     */
    private void updateAttackList() {
        Player mp = getPlayers().getMainPlayer();
        ArrayList<Attack> toRemove = new ArrayList<>();

        // Perform update
        for (Attack atk : getAttacks()) {
            if (atk.update(mp)) {
                // Send a packet if collision is detected
                Packet04Attack pk = new Packet04Attack(atk.getId(),  atk.getOwner().getUID(),
                        atk.getOwner().getType().getNum(), atk.getDirection().getNum(),
                        atk.getX(), atk.getY(), atk.isAliveNum());
                pk.writeDataFrom(client);
            }

            // Clean-up check
            if (!atk.isAlive()) {
                toRemove.add(atk);
            }
        }

        // Perform clean-up
        for (Attack a : toRemove) {
            getAttacks().remove(a.getId());
        }
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

    public ServerThread getServerThread() {
        return server;
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

    public void addEvent(Event e) {
        eventQueue.add(e);
    }
}
