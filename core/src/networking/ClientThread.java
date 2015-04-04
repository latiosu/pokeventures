package networking;

import engine.Core;

import java.io.IOException;
import java.net.*;

public class ClientThread extends Thread {

    private InetAddress ip;
    private DatagramSocket socket;
    private Core core;

    public ClientThread(Core core, String ip) {
        this.core = core;
        try {
            this.socket = new DatagramSocket();
            this.ip = InetAddress.getByName(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet); // Warning: will wait indefinitely
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message = new String(packet.getData()).trim();
            System.out.println("Server > " + message);
        }
    }

    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, 4445);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
