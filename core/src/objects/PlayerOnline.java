package objects;

import java.net.InetAddress;
import java.util.Date;

public class PlayerOnline extends Player {

    private long uid;
    private InetAddress address;
    private int port;

    // Default main player constructor
    public PlayerOnline(String username) {
        this(new Date().getTime(), 0, 0, Direction.DOWN, Type.CHARMANDER, true, username, null, -1);
    }

    public PlayerOnline(long uid, float x, float y, Direction dir, Type t, boolean isMain,
                        String username, InetAddress address, int port) {
        super(uid, t, isMain, username);
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
