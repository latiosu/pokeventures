package objects;

import java.net.InetAddress;

public class PlayerOnline extends Player {

    public InetAddress address;
    public int port;

    // Default main player constructor
    public PlayerOnline(String username) {
        this(0, 0, Direction.DOWN, Type.CHARMANDER, true, username, null, -1);
    }

    public PlayerOnline(float x, float y, Direction dir, Type t, boolean isMain, String username, InetAddress address, int port) {
        super(t, isMain, username);
        this.x = x;
        this.y = y;
        this.direction = dir;
        this.address = address;
        this.port = port;
    }
}
