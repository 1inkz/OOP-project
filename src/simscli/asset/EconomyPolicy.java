package simscli.asset;

/**
 * Policy contract for selecting the economy state by in-game day.
 */
public interface EconomyPolicy {
    EconomyState stateForDay(int dayNumber);
}
