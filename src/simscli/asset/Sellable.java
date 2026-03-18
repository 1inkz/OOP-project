package simscli.asset;

/**
 * Interface for assets that can be sold.
 */
public interface Sellable {
    /**
     * Gets the original purchase value.
     * @return purchase price
     */
    int getValue();
    
    /**
     * Gets the current market sell value.
     * @return selling price (may differ from purchase value)
     */
    int sellValue();
}