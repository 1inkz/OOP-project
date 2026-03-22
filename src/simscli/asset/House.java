package simscli.asset;

/**
 * House asset: unlocks Home location with residential actions.
 */
public class House extends Asset {
    /**
     * Creates a house with ID, name, and purchase value.
     * @param id unique house identifier
     * @param name house display name
     * @param purchaseValue purchase cost
     */
    public House(int id, String name, int purchaseValue) {
        super(id, name, purchaseValue);
    }

    /**
     * Gets the asset type name.
     * @return "House"
     */
    public String getAssetType() {
        return "House";
    }

    /**
     * Gets the sell value of this house (90% of purchase value).
     * @return sell value in Simcoin
     */
    @Override
    public int sellValue() {
        if (purchaseValue < 0) {
            System.out.println("Invalid purchase value for House.");
            return 0;
        }
        return (int) (purchaseValue * 0.9); // Houses can be sold for 90% of their purchase value
    }
}