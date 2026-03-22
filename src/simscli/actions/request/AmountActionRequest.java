package simscli.actions.request;

/**
 * Request payload carrying a single amount for banking actions.
 */
public final class AmountActionRequest implements ActionRequest {
    private final int amount;

    public AmountActionRequest(int amount) {
        this.amount = amount;
    }

    public int amount() {
        return amount;
    }
}
