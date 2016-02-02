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
import engine.structs.Event;
import engine.structs.Message;
import engine.structs.UserList;
import networking.ClientThread;
import networking.packets.*;
import objects.Attack;
import objects.BaseAttack;
import objects.Player;
import objects.Tiles.JumpableTile;
import objects.Tiles.Tile;
import objects.structs.Direction;
import objects.structs.PlayerType;
import objects.structs.State;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


public class ClientCore extends Game {

    public boolean isOnline = false;
    public boolean playMode = false;
    public boolean isHost = false;
    // Object classes
    protected UserList players;
    protected AttackList attacks;
    protected List<Event> events;
    // Engine variables/constants
    private float delta = 0;
    private float debugDelta = 0;
    private float heartbeatDelta = 0;
    private int frames = 0;
    private long bootTime = System.nanoTime();
    // Rendering classes
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private Texture tex;
    private Sprite sprite;

    // Engine classes
    private WorldManager world;
    private EventManager eventManager;
    private UI ui;

    // Networking classes
    private ClientThread client;

    @Override
    public void create() {
        // Object-related
        players = new UserList();
        attacks = new AttackList();
        events = new CopyOnWriteArrayList<>();

        // Engine
        new AssetManager();
        cam = new OrthographicCamera(Config.Camera.VIEWPORT_WIDTH, Config.Camera.VIEWPORT_HEIGHT);
        ui = new UI(this);
        world = new WorldManager();
        eventManager = new EventManager();

        // Start networking
        startNetworking();

        // Input handling
        InputMultiplexer multiplexer = new InputMultiplexer();
        UserInputProcessor input = new UserInputProcessor(this);
        multiplexer.addProcessor(ui.getStage());
        multiplexer.addProcessor(input);
        Gdx.input.setInputProcessor(multiplexer);

        // Overworld data
        batch = new SpriteBatch();
        tex = world.loadWorld(Config.World.MAP);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        sprite = new Sprite(tex);
        sprite.setOrigin(0, 0);

        // Player spawn position
        cam.position.x = Config.World.SPAWN_X;
        cam.position.y = Config.World.SPAWN_Y;
        cam.update();
    }

    @Override
    public void dispose() {
        // Send disconnect packet to server
        client.sendDataToServer(new PacketDisconnect(getPlayers().getMainPlayer().getUid()));
        batch.dispose();
        tex.dispose();
        ui.dispose();
        Logger.log(Logger.Level.INFO, "----- Client thread has shutdown -----\n");
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Game heartbeat
        if (delta > Config.Engine.CLIENT_UPDATE_RATE) {
            if (playMode) {
                updateMainPlayer();
                updateCamera();
            }
            updateEvents(delta);
            delta -= Config.Engine.CLIENT_UPDATE_RATE;
        }

        // Render game world
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        sprite.draw(batch);
        if (playMode) {
            // Reversed loop to render main player last
            for (int i = players.size() - 1; i >= 0; i--) {
                // Render Player sprites
                Player p = (Player) players.get(i);
                p.render(Gdx.graphics.getDeltaTime(), batch);
            }
            for (BaseAttack a : attacks) {
                ((Attack) a).render(Gdx.graphics.getDeltaTime(), batch);
            }
        }
        batch.end();

        // Networking heartbeat
        if (playMode) {
            if (heartbeatDelta >= Config.Networking.HEARTBEAT_RATE) {
                client.sendDataToServer(new PacketHeartbeat(getPlayers().getMainPlayer().getUid()));
                heartbeatDelta -= Config.Networking.HEARTBEAT_RATE;
            }
            heartbeatDelta += Gdx.graphics.getDeltaTime();
        }

        // Debug stuff
        if (Config.DEBUG) {
            if (playMode) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                Player mp = players.getMainPlayer();
                ShapeRenderer sr = new ShapeRenderer();
                sr.setAutoShapeType(true);
                sr.setProjectionMatrix(cam.combined);
                sr.begin(ShapeRenderer.ShapeType.Filled); // BEGIN DEBUG RENDERING

                // Render blocked tile as red
                sr.setColor(1f, 0, 0, 0.3f);
                for (Tile t : world.getBlocked()) {
                    sr.rect(t.getX(), t.getY(), Config.World.TILE_SIZE, Config.World.TILE_SIZE);
                }

                // Render jumpable tiles as respective colours
                for (JumpableTile t : world.getJumpable()) {
                    switch (t.getValidDir()) {
                        case DOWN:
                            sr.setColor(0, 1f, 0, 0.3f); // DOWN = green
                            break;
                        case LEFT:
                            sr.setColor(0, 0, 1f, 0.3f); // LEFT = blue
                            break;
                        default:
                            Logger.log(Logger.Level.ERROR,
                                    "Colour has not been set for this JumpableTile direction! (%s)\n",
                                    t.getValidDir());
                            break;
                    }
                    sr.rect(t.getX(), t.getY(), Config.World.TILE_SIZE, Config.World.TILE_SIZE);
                }


                // Render player centre tile as white
                Tile a = world.getTile(mp.getX(), mp.getY());
                sr.setColor(1f, 1f, 1f, 0.4f);
                sr.rect(a.getX(), a.getY(), Config.World.TILE_SIZE, Config.World.TILE_SIZE);

                // Render player outline as yellow
                sr.set(ShapeRenderer.ShapeType.Line);
                sr.setColor(Color.YELLOW);
                sr.rect(mp.getX(), mp.getY(), Config.Character.CHAR_COLL_WIDTH, Config.Character.CHAR_COLL_HEIGHT);

                // Render projectile outline as red
                sr.setColor(1f, 0, 0, 0.3f);
                for (BaseAttack atk : attacks) {
                    sr.rect(atk.getPosX(), atk.getPosY(), Config.World.TILE_SIZE, Config.World.TILE_SIZE);
                }

                sr.end(); // END DEBUG RENDERING

                // Debug Printing
                if (debugDelta >= Config.Engine.DEBUG_LOG_RATE) {
                    long currentTime = System.nanoTime() - bootTime;
                    String timeString = String.format("%02d:%02d:%02d",
                            TimeUnit.NANOSECONDS.toHours(currentTime),
                            TimeUnit.NANOSECONDS.toMinutes(currentTime) % 60,
                            TimeUnit.NANOSECONDS.toSeconds(currentTime) % 60);
                    Logger.log(Logger.Level.INFO,
                            "FPS=%d - Events=%d - Attacks=%d - Users=%d (%s)\n",
                            frames / 3,
                            events.size(),
                            attacks.size(),
                            players.size(),
                            timeString);
                    debugDelta -= Config.Engine.DEBUG_LOG_RATE;
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

    private void updateEvents(float delta) {
        List<Event> finished = new CopyOnWriteArrayList<>();
        // Update queued events first
        for (Event e : events) {
            // Finished events
            if (e.update(delta)) {
                finished.add(e);
            }
        }
        // Clean up finished events
        for (Event e : finished) {
            events.remove(e);
        }
    }

    public void startNetworking() {
        Logger.log(Logger.Level.INFO, "----- Client thread started -----\n");
        client = new ClientThread(this, Config.Networking.SERVER_IP);
        client.start();
        isOnline = true;
    }

    public void initMainPlayer(String username) {
        Player p = new Player(Config.Character.DEFAULT_TYPE, username); // <----- NOTE DEFAULT TYPE USED HERE
        players.setMainPlayer(p);
        players.add(p.getUid(), p);
        PacketServerLogin pk = new PacketServerLogin(p.getUid(), p.getUsername(), p.getType().getNum());
        client.sendDataToServer(pk); // Send packet to server to sync main player
        playMode = true;
    }

    private void updateCamera() {
        Player mp = getPlayers().getMainPlayer();

        cam.position.x = mp.getX();
        cam.position.y = mp.getY();
        cam.update();

        // This camera algorithm only follows player if camera can be centered.
        if (cam.position.x > Config.Camera.CAM_MAX_X) {
            cam.position.x = Config.Camera.CAM_MAX_X;
        }
        if (cam.position.y > Config.Camera.CAM_MAX_Y) {
            cam.position.y = Config.Camera.CAM_MAX_Y;
        }
        if (cam.position.x < Config.Camera.CAM_MIN_X) {
            cam.position.x = Config.Camera.CAM_MIN_X;
        }
        if (cam.position.y < Config.Camera.CAM_MIN_Y) {
            cam.position.y = Config.Camera.CAM_MIN_Y;
        }
        cam.update();
    }

    private void updateMainPlayer() {
        Player mp = getPlayers().getMainPlayer();

        // Trigger fainting and respawn
        if (!mp.isAlive()) {
            if (mp.getState() != State.FAINTED) {
                mp.setState(State.FAINTED);
                PacketMove pk = new PacketMove(mp.getUid(),
                        mp.getUsername(),
                        mp.getX(),
                        mp.getY(),
                        mp.getHp(),
                        mp.getMaxHp(),
                        mp.getState().getNum(),
                        mp.getDirection().getNum(),
                        mp.getType().getNum(),
                        mp.isAlive()
                );
                client.sendDataToServer(pk);

                ui.triggerRespawnScreen();
            }

            // Ignore player input
            return;
        }

        // Resolve player jump movement
        if (mp.getState() == State.JUMP) {
            switch (mp.getDirection()) {
                case DOWN:
                    if (mp.getY() > 0) {
                        mp.setY(mp.getY() - (Config.Character.WALK_DIST / 2f));
                    }
                    break;
                case LEFT:
                    if (mp.getX() > 0) {
                        mp.setX(mp.getX() - (Config.Character.WALK_DIST / 2f));
                    }
                    break;
                default:
                    Logger.log(Logger.Level.ERROR,
                            "This jump movement has not been implemented yet (%s)\n",
                            mp.getDirection());
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
                PacketAttack pk = new PacketAttack(atk.getId(),
                        atk.getOwner().getUid(),
                        atk.getOwner().getType().getNum(),
                        atk.getDirection().getNum(),
                        atk.getX(),
                        atk.getY(),
                        atk.isAliveNum());
                client.sendDataToServer(pk);
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
                        mp.setY(mp.getY() - Config.Character.WALK_DIST);
                    }
                    break;
                case LEFT:
                    if (mp.getX() > 0) {
                        mp.setX(mp.getX() - Config.Character.WALK_DIST);
                    }
                    break;
                case UP:
                    if (mp.getY() < AssetManager.level.getHeight() - Config.Character.CHAR_COLL_HEIGHT) {
                        mp.setY(mp.getY() + Config.Character.WALK_DIST);
                    }
                    break;
                case RIGHT:
                    if (mp.getX() < AssetManager.level.getWidth() - Config.Character.CHAR_COLL_WIDTH) {
                        mp.setX(mp.getX() + Config.Character.WALK_DIST);
                    }
                    break;
            }
        }

        // Update player animations immediately for responsiveness
        mp.getAnim().updateAnim();

        // <----- Play sounds immediately for responsiveness in the future


        // Handle collision for main player
        Tile collidedTile = getWorldManager().handleCollision(mp);

        // Send movement packet with freshly updated main player data
        PacketMove pk = new PacketMove(mp.getUid(),
                mp.getUsername(),
                mp.getX(),
                mp.getY(),
                mp.getHp(),
                mp.getMaxHp(),
                mp.getState().getNum(),
                mp.getDirection().getNum(),
                mp.getType().getNum(),
                mp.isAlive()
        );
        client.sendDataToServer(pk);
    }

    public void handlePlayerPacket(long uid, String username, float x, float y, float hp, float maxHp,
                                   State state, Direction dir, PlayerType type) {
        Player p = (Player) getPlayers().get(uid);
        if (p != null) {
            p.setX(x);
            p.setY(y);
            p.setHp(hp);
            p.setMaxHp(maxHp);
            p.setState(state);
            p.setDirection(dir);
            p.setType(type);
            p.getAnim().updateAnim();
        } else {
            Logger.log(Logger.Level.ERROR,
                    "User not found - %s\n",
                    username);
        }
    }

    public void handleAttackPacket(long id, long uid, Direction dir, float x, float y, boolean isAlive) {
        Attack atk = (Attack) getAttacks().get(id);
        if (atk != null) {
            // Update attack if registered
            if (!atk.isAlive() || !isAlive) { // Remove if not alive
                getAttacks().remove(id);
            } else {
                atk.setX(x);
                atk.setY(y);
                atk.setDirection(dir);
                atk.setAlive(isAlive);
            }
        } else {
            // Store attack if not registered
            atk = new Attack(id, (Player) getPlayers().get(uid), dir, x, y);
            attacks.add(id, atk);
        }
    }

    /**
     * Stores message inside chat client and updates UI components.
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

    public void addEvent(Event e) {
        events.add(e);
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public synchronized UserList getPlayers() {
        return this.players;
    }

    public synchronized AttackList getAttacks() {
        return this.attacks;
    }
}
