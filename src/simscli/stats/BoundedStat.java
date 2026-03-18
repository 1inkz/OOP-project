package simscli.stats;

/**
 * Represents a 0-100 bounded stat with automatic clamping.
 * Used for needs, pet stats, and other bounded values.
 */
public final class BoundedStat {
    private int value;

    /**
     * Creates a new bounded stat with initial value.
     * @param initial the starting value (auto-clamped to 0-100)
     */
    public BoundedStat(int initial) {
        set(initial);
    }

    public int get() {
        return value;
    }
    
    /**
     * Sets the value without clamping (internal use).
     * @param v the raw value to set
     */
    public void setValue(int v) {
        this.value = v;
    }

    /**
     * Sets the value, clamping to 0-100 range.
     * @param v the value to set
     */
    public void set(int v) {
        if (v < 0) value = 0;
        else if (v > 100) value = 100;
        else value = v;
    }

    /**
     * Adds to the current value, clamping result to 0-100.
     * @param delta the amount to add (positive or negative)
     */
    public void add(int delta) {
        set(value + delta);
    }

    /**
     * Checks if stat is at zero.
     * @return true if value equals 0
     */
    public boolean isZero() {
        return value == 0;
    }

    /**
     * Checks if stat is in critical state.
     * @return true if value <= 15
     */
    public boolean isCritical() {
        return value <= 15;
    }
}