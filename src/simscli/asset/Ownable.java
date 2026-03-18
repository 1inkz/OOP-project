package simscli.asset;

/**
 * Interface for ownable assets.
 */
public interface Ownable {
    /**
     * Gets the asset's unique identifier.
     * @return asset ID
     */
    int getId();
    
    /**
     * Gets the asset's display name.
     * @return asset name
     */
    String getName();
}