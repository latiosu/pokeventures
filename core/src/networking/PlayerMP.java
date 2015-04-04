package networking;

import engine.AssetManager;
import engine.KeyboardProcessor;
import objects.Player;
import objects.Type;

import java.net.InetAddress;

public class PlayerMP extends Player {

    private KeyboardProcessor input;
    public InetAddress ip;
    public int port;

    public PlayerMP(Type t, AssetManager assets, boolean isMain, KeyboardProcessor input, InetAddress ip, int port) {
        super(t, assets, isMain);
        this.input = input; // Do I need a separate input handler??
        this.ip = ip;
        this.port = port;
    }
}
