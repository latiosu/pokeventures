package engine.structs;

import java.util.function.Consumer;

public class Event {

    private float lastDelta = 0;
    private float duration;
    private float updateRate;
    private Consumer<Object> block;

    public Event (float duration, float updateRate, Consumer<Object> block) {
        this.duration = duration;
        this.updateRate = updateRate;
        this.block = block;
    }

    public boolean update(float delta) {
        lastDelta += delta;
        duration -= delta;

        if (duration <= 0) {
            block.accept(0);
            return true;
        } else if (lastDelta >= updateRate) {
            block.accept(duration);
            lastDelta -= updateRate;
        }
        return false;
    }

}
