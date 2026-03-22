package simscli.asset;

/**
 * Hotel asset: generates 250 Simcoin daily passive income.
 */
public class Hotel extends Asset {
    private static final int BASE_DAILY_INCOME = 200;
    private static final int MAX_LEVEL = 3;
    private int level;

    public Hotel(int id, String name, int purchaseValue) {
        super(id, name, purchaseValue);
        this.level = 1;
    }

    @Override
    public String getAssetType() {
        return "Hotel";
    }

    /**
     * Gets the current upgrade level of this hotel (1-3).
     * @return hotel level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Checks if this hotel can be upgraded to the next level.
     * @return true if level < MAX_LEVEL, false otherwise
     */
    public boolean canUpgrade() {
        return level < MAX_LEVEL;
    }

    /**
     * Gets the cost to upgrade to the next level.
     * @return upgrade cost in Simcoin, or 0 if at max level
     */
    public int getUpgradeCost() {
        if (!canUpgrade()) {
            return 0;
        }
        return level == 1 ? 1500 : 3000;
    }

    /**
     * Upgrades the hotel to the next level if possible.
     * @return true if upgrade successful, false if at max level
     */
    public boolean upgrade() {
        if (!canUpgrade()) {
            return false;
        }
        level++;
        return true;
    }

    /**
     * Calculates daily income based on current level and economic conditions.
     * Base income multiplied by level and economy state multiplier.
     * @param state the current economy state (null defaults to NORMAL)
     * @return calculated daily income in Simcoin
     */
    public int calculateDailyIncome(EconomyState state) {
        double multiplier = state == null ? EconomyState.NORMAL.multiplier() : state.multiplier();
        double income = BASE_DAILY_INCOME * level * multiplier;
        return Math.max(0, (int) Math.round(income));
    }

    /**
     * Gets the sell value of this hotel (120% of purchase value - hotels appreciate).
     * @return sell value in Simcoin
     */
    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for Hotel.");
            return 0;
        }
        return (int) (purchaseValue * 1.2); // Hotels can be sold for a profit, hence 120% of purchase value (Rich get richer :D)
    }
}