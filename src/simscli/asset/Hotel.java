package simscli.asset;

public class Hotel extends Asset {
    public Hotel(int id, String name, int purchaseValue) {
        super(id, name, purchaseValue);
    }

    @Override
    public String getAssetType() {
        return "Hotel";
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