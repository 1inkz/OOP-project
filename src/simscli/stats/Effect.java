package simscli.stats;

import java.util.EnumMap;
import java.util.Map;

/**
 * Immutable-ish effect: a set of deltas applied to needs.
 * TDD-friendly: easy to assert exact deltas in tests.
 */
public final class Effect {
    private final EnumMap<NeedType, Integer> deltas;

    private Effect(EnumMap<NeedType, Integer> deltas) {
        this.deltas = deltas;
    }

    public static Effect none() {
        return new Effect(new EnumMap<>(NeedType.class));
    }

    public Effect plus(NeedType need, int delta) {
        EnumMap<NeedType, Integer> copy = new EnumMap<>(deltas);
        copy.put(need, copy.getOrDefault(need, 0) + delta);
        return new Effect(copy);
    }
    


    public Map<NeedType, Integer> deltas() {
        return new EnumMap<>(deltas);
    }

    @Override
    public String toString() {
        return deltas.toString();
    }

}