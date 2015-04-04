package networking;

import engine.Config;
import objects.Player;
import objects.Type;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlayerMP extends Player {

    public InetAddress address;
    public int port;

    public PlayerMP(Type t, boolean isMain, String username, InetAddress address, int port) {
        super(t, isMain, username);
        this.address = address;
        this.port = port;
    }
}
