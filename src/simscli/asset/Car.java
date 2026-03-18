package simscli.asset;

/**
 * Car asset: can be owned and used for travel, reducing fatigue cost.
 */
public class Car extends Asset {
	
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

    public String getAssetType() {
        return "Car";
    }

    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for Car.");
            return 0;
        }
        return (int) (purchaseValue * 0.6); 
    }
}