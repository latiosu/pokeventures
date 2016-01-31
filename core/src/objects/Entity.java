package objects;

import objects.structs.PlayerType;
import objects.structs.State;

public abstract class Entity extends GameObject {

    // TODO: Check if boolean isHit is used
    protected PlayerType type;
    protected boolean isNewState;
//    protected boolean isHit;
    protected State state;

    protected Entity(PlayerType type) {
        super();
        this.type = type;
//        this.isHit = false;
        this.state = State.IDLE;
    }

    // Getters and setters
    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

//    public boolean isHit() {
//        return isHit;
//    }
//
//    public void setHit(boolean isHit) {
//        this.isHit = isHit;
//    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != state) {
            isNewState = true;
            this.state = state;
        }
    }

    public boolean isNewState() {
        return isNewState;
    }

    public void setNewState(boolean isNewState) {
        this.isNewState = isNewState;
    }
}
