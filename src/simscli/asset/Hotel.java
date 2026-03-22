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

    public int getLevel() {
        return level;
    }

    public boolean canUpgrade() {
        return level < MAX_LEVEL;
    }

    public int getUpgradeCost() {
        if (!canUpgrade()) {
            return 0;
        }
        return level == 1 ? 1500 : 3000;
    }

    public boolean upgrade() {
        if (!canUpgrade()) {
            return false;
        }
        level++;
        return true;
    }

    public int calculateDailyIncome(EconomyState state) {
        double multiplier = state == null ? EconomyState.NORMAL.multiplier() : state.multiplier();
        double income = BASE_DAILY_INCOME * level * multiplier;
        return Math.max(0, (int) Math.round(income));
    }

    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for Hotel.");
            return 0;
        }
        return (int) (purchaseValue * 1.2); // Hotels can be sold for a profit, hence 120% of purchase value (Rich get richer :D)
    }
}