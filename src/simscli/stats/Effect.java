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

    /**
     * Creates an empty effect with no stat changes.
     * @return new empty Effect
     */
    public static Effect none() {
        return new Effect(new EnumMap<>(NeedType.class));
    }

    /**
     * Returns a new Effect with an additional need delta.
     * @param need the need type to modify
     * @param delta the amount to change
     * @return new Effect with the added delta
     */
    public Effect plus(NeedType need, int delta) {
        EnumMap<NeedType, Integer> copy = new EnumMap<>(deltas);
        copy.put(need, copy.getOrDefault(need, 0) + delta);
        return new Effect(copy);
    }
    


    /**
     * Gets the map of need deltas in this effect.
     * @return defensive copy of deltas
     */
    public Map<NeedType, Integer> deltas() {
        return new EnumMap<>(deltas);
    }

    @Override
    public String toString() {
        return deltas.toString();
    }

}