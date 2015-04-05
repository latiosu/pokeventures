package engine;

import networking.packets.Packet03Chat;

import java.util.LinkedList;

public class ChatClient {

    private LinkedList<String> messages;

    public ChatClient() {
        messages = new LinkedList<String>();
    }

    public void sendMessage(String msg) {

        messages.add(msg);
    }

    public void receiveMessage(Packet03Chat packet) {

    }

    public String getLastMessage() {
        return messages.peekLast();
    }
}
