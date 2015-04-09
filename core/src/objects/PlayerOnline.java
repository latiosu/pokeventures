package objects;

import engine.Config;

import java.net.InetAddress;
import java.util.Date;

public class PlayerOnline extends Player {

    private InetAddress address;
    private int port;

    // Default main player constructor
    public PlayerOnline(String username) {
        this(new Date().getTime(), Config.SPAWN_X, Config.SPAWN_Y,
                Direction.DOWN, PlayerType.CHARMANDER, username, null, -1);
    }

    public PlayerOnline(long uid, float x, float y, Direction dir, PlayerType t,
                        String username, InetAddress address, int port) {
        super(uid, t, username);
        this.x = x;
        this.y = y;
        this.direction = dir;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }
    public void setAddress(InetAddress address) {
        this.address = address;
    }
    public void setPort(int port) {
        this.port = port;
    }
}
