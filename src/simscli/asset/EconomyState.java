package simscli.asset;

/**
 * Daily economy state that drives hotel income multipliers.
 */
public enum EconomyState {
    SLUMP(0.70),
    NORMAL(1.00),
    FESTIVAL(1.25),
    BOOM(1.50),
    RECOVERY(0.90);

    private final double multiplier;

    EconomyState(double multiplier) {
        this.multiplier = multiplier;
    }

    public double multiplier() {
        return multiplier;
    }
}
