package trait;

import action.Action;
import action.Outcome;
import sim.Sim;

public interface Trait {
    String name();

    default boolean allowAction(Action action, Sim sim){
        return true;
    }

    default void modifyOutcome(Action action, Sim sim, Outcome outcome){
    }

}
