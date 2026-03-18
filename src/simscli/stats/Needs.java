package simscli.stats;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages all six Sim needs: hunger, energy, hygiene, social, fun, and bladder.
 * Initializes all needs to reasonable starting values.
 */
public final class Needs {
    private final EnumMap<NeedType, BoundedStat> stats = new EnumMap<>(NeedType.class);

    public Needs() {
        // start "okay-ish"
        stats.put(NeedType.HUNGER,  new BoundedStat(70));
        stats.put(NeedType.ENERGY,  new BoundedStat(70));
        stats.put(NeedType.HYGIENE, new BoundedStat(70));
        stats.put(NeedType.SOCIAL,  new BoundedStat(65));
        stats.put(NeedType.FUN,     new BoundedStat(60));
        stats.put(NeedType.BLADDER, new BoundedStat(60));
    }

    /**
     * Gets the current value of a specific need.
     * @param t the need type to query
     * @return current need value (0-100)
     */
    public int get(NeedType t) {
        return stats.get(t).get();
    }
    
    /**
     * Sets a need to a specific value, clamping to 0-100.
     * @param type the need type to set
     * @param value the new value
     */
    public void set(NeedType type, int value) {
        BoundedStat stat = stats.get(type);
        if (stat != null) {
            stat.setValue(Math.max(0, Math.min(100, value)));
        }
    }

    /**
     * Modifies a need by the given delta.
     * @param t the need type to modify
     * @param delta amount to add (positive or negative)
     */
    public void add(NeedType t, int delta) {
        stats.get(t).add(delta);
    }

    /**
     * Checks if a need is at zero (critical condition).
     * @param t the need type to check
     * @return true if need value is 0
     */
    public boolean isZero(NeedType t) {
        return stats.get(t).isZero();
    }

    /**
     * Checks if a need is in critical state.
     * @param t the need type to check
     * @return true if need value &lt;= 15
     */
    public boolean isCritical(NeedType t) {
        return stats.get(t).isCritical();
    }

    /**
     * Returns a defensive copy of all current need values.
     * @return immutable snapshot of needs state
     */
    public Map<NeedType, Integer> snapshot() {
        EnumMap<NeedType, Integer> m = new EnumMap<>(NeedType.class);
        for (Map.Entry<NeedType, BoundedStat> e : stats.entrySet()) {
            m.put(e.getKey(), e.getValue().get());
        }
        return m;
    }
}
