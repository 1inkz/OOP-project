package simscli.asset;

public class House extends Asset {
    public House(int id, String name, int purchaseValue) {
        super(id, name, purchaseValue);
    }

    public String getAssetType() {
        return "House";
    }

    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for House.");
            return 0;
        }
        return (int) (purchaseValue * 0.9); 
    }
}