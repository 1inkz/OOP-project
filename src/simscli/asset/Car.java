package simscli.asset;

/**
 * Car asset: can be owned and used for travel, reducing fatigue cost.
 */
public class Car extends Asset {
	
    private static final int DAILY_MAINTENANCE_COST = 20;
    private double travelMultiplier;

    /**
     * Creates a car with travel multiplier for efficiency.
     * @param id unique car identifier
     * @param name car display name
     * @param purchaseValue purchase cost
     * @param travelMultiplier travel cost multiplier (must be positive)
     * @throws IllegalArgumentException if multiplier is NaN/Infinite
     */
    public Car(int id, String name, int purchaseValue, double travelMultiplier) {
        super(id, name, purchaseValue);
        if (Double.isNaN(travelMultiplier) || Double.isInfinite(travelMultiplier)) {
        	throw new IllegalArgumentException("travelMultiplier cannot be NaN/Infinity.");
        }
            
        this.travelMultiplier = travelMultiplier > 0 ? travelMultiplier : 1.0;
    }

    /**
     * Gets the travel cost multiplier for this car.
     * @return travel multiplier
     */
    public double getTravelMultiplier() {
        return travelMultiplier;
    }

    /**
     * Gets the daily maintenance cost for this car.
     * @return daily maintenance cost in Simcoin
     */
    public int getDailyMaintenanceCost() {
        return DAILY_MAINTENANCE_COST;
    }

    /**
     * Gets the asset type name.
     * @return "Car"
     */
    public String getAssetType() {
        return "Car";
    }

    /**
     * Gets the sell value of this car (60% of purchase value).
     * @return sell value in Simcoin
     */
    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for Car.");
            return 0;
        }
        return (int) (purchaseValue * 0.6); 
    }
}