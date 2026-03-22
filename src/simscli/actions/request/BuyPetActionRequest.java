package simscli.actions.request;

/**
 * Request payload for pet purchase flow.
 */
public final class BuyPetActionRequest implements ActionRequest {
    private final int petSelection;
    private final String petName;

    public BuyPetActionRequest(int petSelection, String petName) {
        this.petSelection = petSelection;
        this.petName = petName;
    }

    public int petSelection() {
        return petSelection;
    }

    public String petName() {
        return petName;
    }
}
