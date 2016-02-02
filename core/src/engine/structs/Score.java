package engine.structs;

public class Score implements Comparable<Score> {

    public long uid;
    public int score;

    public Score(long uid, int score) {
        this.uid = uid;
        this.score = score;
    }

    @Override
    public int compareTo(Score s) {
        return score - s.score;
    }
}
