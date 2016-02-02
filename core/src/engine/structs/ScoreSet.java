package engine.structs;

import objects.BasePlayer;

import java.util.*;


public class ScoreSet implements Iterable<Score> {

    private java.util.List<Score> scores;

    public ScoreSet() {
        scores = new ArrayList<>();
    }

    public ScoreSet(UserList players) {
        this();
        for (BasePlayer p : players) {
            scores.add(new Score(p.getUid(), p.getScore()));
        }
    }

    public void addScore(Score score) {
        scores.add(score);
    }

    /**
     * Returns a sorted list of scores in INCREASING order.
     */
    public java.util.List<Score> sorted() {
        java.util.List<Score> sorted = scores.subList(0, scores.size());
        Collections.sort(sorted);
        return sorted;
    }

    private class ScoreSetIterator implements Iterator<Score> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < scores.size();
        }

        @Override
        public Score next() {
            Score result = scores.get(index);
            index++;
            return result;
        }
    }

    @Override
    public Iterator<Score> iterator() {
        return new ScoreSetIterator();
    }

    public java.util.List<Score> getScores() {
        return scores;
    }
}
