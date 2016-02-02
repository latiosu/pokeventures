package engine.structs;

import java.util.function.Predicate;

public class Event<T, R> {

    private boolean isTimed;
    private float lastDelta = 0;
    private float duration = 0;
    private float updateRate = 0;
    private Predicate<Object> block;

    /**
     * Creates a timed event that will check the Predicate every so often (specified by update rate)
     * for as long as the specified duration.
     *
     * @param duration - total time spent alive for updates in seconds
     * @param updateRate - time elapsed between each update in seconds
     * @param block - task to execute per update
     */
    public Event(float duration, float updateRate, Predicate<Object> block) {
        this.isTimed = true;
        this.duration = duration;
        this.updateRate = updateRate;
        this.block = block;
    }

    /**
     * Convenience constructor that has a default update rate of 1 per second.
     */
    public Event(float duration, Predicate<Object> block) {
        this(duration, 1f, block);
    }

    /**
     * Creates an un-timed event that will check the Predicate every game heartbeat.
     * @param block - task to execute per heartbeat
     */
    public Event(Predicate<Object> block) {
        this.isTimed = false;
        this.block = block;
    }

    public boolean update(float delta) {
        if (isTimed) {
            lastDelta += delta;
            duration -= delta;

            if (duration <= 0) {
                return block.test(0);
            } else if (lastDelta >= updateRate) {
                block.test(duration); // <--- Will this be useful?
                lastDelta -= updateRate;
            }
            return false;
        } else {
            return block.test(delta);
        }
    }

}
