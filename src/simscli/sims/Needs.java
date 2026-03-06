package simscli.sims;

import simscli.stats.BoundedStat;
import simscli.stats.NeedType;

import java.util.EnumMap;
import java.util.Map;

public final class Needs {
    private final EnumMap<NeedType, BoundedStat> stats = new EnumMap<>(NeedType.class);

    public Needs() {
        // start “okay-ish”
        stats.put(NeedType.HUNGER,  new BoundedStat(70));
        stats.put(NeedType.ENERGY,  new BoundedStat(70));
        stats.put(NeedType.HYGIENE, new BoundedStat(70));
        stats.put(NeedType.SOCIAL,  new BoundedStat(65));
        stats.put(NeedType.FUN,     new BoundedStat(60));
        stats.put(NeedType.BLADDER, new BoundedStat(60));
    }

    public int get(NeedType t) {
        return stats.get(t).get();
    }

    public void add(NeedType t, int delta) {
        stats.get(t).add(delta);
    }

    public boolean isZero(NeedType t) {
        return stats.get(t).isZero();
    }

    public boolean isCritical(NeedType t) {
        return stats.get(t).isCritical();
    }

    /** For printing only (defensive copy style). */
    public Map<NeedType, Integer> snapshot() {
        EnumMap<NeedType, Integer> m = new EnumMap<>(NeedType.class);
        for (Map.Entry<NeedType, BoundedStat> e : stats.entrySet()) {
            m.put(e.getKey(), e.getValue().get());
        }
        return m;
    }
}