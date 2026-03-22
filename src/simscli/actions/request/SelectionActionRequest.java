package simscli.actions.request;

/**
 * Request payload carrying a menu selection index.
 */
public final class SelectionActionRequest implements ActionRequest {
    private final int selection;

    public SelectionActionRequest(int selection) {
        this.selection = selection;
    }

    public int selection() {
        return selection;
    }
}
