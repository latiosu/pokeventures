package networking;

import engine.Core;

import java.io.IOException;
import java.net.*;

public class ServerThread {

    private DatagramSocket socket;
    private Core core;

    public ServerThread(Core core, String ip) {
        this.core = core;
        try {
            this.socket = new DatagramSocket(4445); // listen on port 4445
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet); // Warning: will wait indefinitely
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message = new String(packet.getData());
            System.out.println("Client > " + message);
            if(message.equalsIgnoreCase("ping")){
                 sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
            }
        }
    }

    public void sendData(byte[] data, InetAddress ip, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, 4445);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
