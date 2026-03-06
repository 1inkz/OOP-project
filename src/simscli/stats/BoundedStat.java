package simscli.stats;

/** Encapsulated 0..100 stat. */
public final class BoundedStat {
    private int value;

    public BoundedStat(int initial) {
        set(initial);
    }

    public int get() {
        return value;
    }

    public void set(int v) {
        if (v < 0) value = 0;
        else if (v > 100) value = 100;
        else value = v;
    }

    public void add(int delta) {
        set(value + delta);
    }

    public boolean isZero() {
        return value == 0;
    }

    public boolean isCritical() {
        return value <= 15;
    }
}