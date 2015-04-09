package networking.packets;

import networking.ClientThread;
import networking.ServerThread;

public class Packet01Disconnect extends Packet {

    private long uid;

    public Packet01Disconnect(byte[] data) {
        super(1);
        this.uid = Long.parseLong(readData(data));
    }

    public Packet01Disconnect(long uid) {
        super(1);
        this.uid = uid;
    }

    @Override
    public void writeDataFrom(Thread thread) {
        if (thread instanceof ClientThread)
            ((ClientThread) thread).sendData(getData());
        else if (thread instanceof ServerThread)
            ((ServerThread) thread).sendDataToAllClients(getData());
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return ("01" + this.uid).getBytes();
    }

    public long getUID() {
        return this.uid;
    }
}
