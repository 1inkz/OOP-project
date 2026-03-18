package simscli.pets;

/**
 * Enum of available pet types with display names and prices.
 */
public enum PetType {
    DOG("Dog", 200),
    CAT("Cat", 200),
    BUNNY("Bunny", 150);

    private final String displayName;
    private final int price;

    /**
     * Creates a pet type with display name and purchase price.
     * @param displayName user-friendly name
     * @param price purchase cost in Simcoin
     */
    PetType(String displayName, int price) {
        this.displayName = displayName;
        this.price = price;
    }

    public String getDisplayName() { return displayName; }
    public int getPrice() { return price; }
}
