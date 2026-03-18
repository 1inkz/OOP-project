package simscli.asset;

/**
 * Abstract base class for all ownable assets: cars, houses, hotels.
 * Assets have purchase value and can be sold.
 */
public abstract class Asset implements Ownable, Sellable {
    private final int id;
    private String name;
    protected int purchaseValue;

    /**
     * Creates an asset with ID, name, and purchase value.
     * @param id unique asset identifier (must be positive)
     * @param name display name (non-empty)
     * @param purchaseValue acquisition cost (non-negative)
     * @throws IllegalArgumentException if validation fails
     */
    public Asset(int id, String name, int purchaseValue) {
        if (id <= 0) {
        	throw new IllegalArgumentException("Asset id must be positive.");
        }
        
        if (name == null || name.trim().isEmpty()) {
        	throw new IllegalArgumentException("Asset name cannot be empty.");
        }
        
        if (purchaseValue < 0) {
        	throw new IllegalArgumentException("Purchase value cannot be negative.");
        }
        
        this.id = id;
        this.name = name;
        this.purchaseValue = purchaseValue;
    }

    public boolean isCar() { return this instanceof Car; }
    public boolean isHouse() { return this instanceof House; }
    public boolean isHotel() { return this instanceof Hotel; }

    @Override
    public int getValue() { return purchaseValue; }

    @Override
    public int getId() { return id; }

    @Override
    public String getName() { return name; }

    /**
     * Gets the asset type name.
     * @return asset type string (Car, House, Hotel)
     */
    public abstract String getAssetType();
}