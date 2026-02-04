public class Car extends Asset{
    private double travelMultiplier;

    public Car(int id, String name, int purchaseValue, double travelMultiplier){
        super(id, name, purchaseValue);
        this.travelMultiplier = travelMultiplier;
    }

    public double getTravelMultiplier(){
        return travelMultiplier;
    }

    @Override
    public String getAssetType(){
        return "Car";
    }

    // Cars depreciate faster
    @Override
    public int sellValue(){
        return (int)(purchaseValue * 0.6);
    }
}
