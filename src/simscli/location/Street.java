package simscli.location;

import simscli.actions.Action;
import simscli.sims.Sim;
import java.util.Collections;
import java.util.List;

/**
 * Street location: starting area with no actions, used for navigation.
 */
public final class Street extends Location {
    @Override
    public String key() {
        return "street";
    }

    @Override
    public String name() {
        return "Street";
    }

    // For new created sims initial location purpose
    @Override
    public List<Action> actions(Sim sim) {
        return Collections.emptyList();
    }

    @Override
    public String onEnter(Sim sim) {
        return sim.getName() + " is on the street. Travel to other locations to perform actions.";
    }
}