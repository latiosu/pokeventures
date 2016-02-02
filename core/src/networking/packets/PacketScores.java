package networking.packets;

import engine.structs.Score;
import engine.structs.ScoreSet;

public class PacketScores extends Packet {

    private ScoreSet scores;
    private String compressedScores;

    public PacketScores(byte[] data) {
        super(PacketType.SCORES.getId());
        String[] dataArray = readData(data).split(DL);
        scores = new ScoreSet();

        // Parse compressed score data (uid DL score) * players
        for (int i = 0; i < dataArray.length; i += 2) {
            long uid = Long.parseLong(dataArray[i]);
            int score = Integer.parseInt(dataArray[i + 1]);
            scores.addScore(new Score(uid, score));
        }
    }

    public PacketScores(ScoreSet set) {
        super(PacketType.SCORES.getId());
        compressedScores = "";

        // Compress score data
        for (Score s : set) {
            compressedScores += s.uid + DL + s.score + DL;
        }
        // Remove trailing DL
        if (compressedScores.length() > 0) {
            compressedScores = compressedScores.substring(0, compressedScores.length() - 1);
        }
    }

    @Override
    /* Reminder: Update this when changing packet structure */
    public byte[] getData() {
        return (PacketType.SCORES.getIdString() + this.compressedScores).getBytes();
    }

    public ScoreSet getScores() {
        return scores;
    }
}
