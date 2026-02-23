package action;

public interface Action {
    String name();
    boolean canPerform(ActionContext ctx);
    Outcome perform(ActionContext ctx);
}
