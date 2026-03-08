package simscli.asset;

public class Car extends Asset {
	
    private double travelMultiplier;

    public Car(int id, String name, int purchaseValue, double travelMultiplier) {
        super(id, name, purchaseValue);
        if (Double.isNaN(travelMultiplier) || Double.isInfinite(travelMultiplier)) {
        	throw new IllegalArgumentException("travelMultiplier cannot be NaN/Infinity.");
        }
            
        this.travelMultiplier = travelMultiplier > 0 ? travelMultiplier : 1.0;
    }

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