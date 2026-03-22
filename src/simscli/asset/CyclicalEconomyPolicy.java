package simscli.asset;

/**
 * Deterministic 7-day economy cycle for predictable gameplay and tests.
 */
public final class CyclicalEconomyPolicy implements EconomyPolicy {
    @Override
    public EconomyState stateForDay(int dayNumber) {
        int cycle = Math.floorMod(dayNumber, 7);
        switch (cycle) {
            case 1:
            case 2:
            case 3:
                return EconomyState.NORMAL;
            case 4:
                return EconomyState.BOOM;
            case 5:
                return EconomyState.FESTIVAL;
            case 6:
                return EconomyState.SLUMP;
            default:
                return EconomyState.RECOVERY;
        }
    }
}
