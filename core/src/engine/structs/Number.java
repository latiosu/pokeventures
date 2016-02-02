package engine.structs;

public class Number {

    private int value;

    public Number(int value) {
        this.value = value;
    }

    public void add(int amount) {
        value += amount;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
