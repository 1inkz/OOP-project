package simscli.pets;

public enum PetType {
    DOG("Dog", 200),
    CAT("Cat", 200),
    BUNNY("Bunny", 150);

    private final String displayName;
    private final int price;

    PetType(String displayName, int price) {
        this.displayName = displayName;
        this.price = price;
    }

    public String getDisplayName() { return displayName; }
    public int getPrice() { return price; }
}
