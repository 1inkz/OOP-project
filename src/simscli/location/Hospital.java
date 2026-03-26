package simscli.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;

/**
 * Hospital location: emergency care services and Doctor employment.
 */
public final class Hospital extends Location {
    @Override
    public String key() {
        return "hospital";
    }

    @Override
    public String name() {
        return "Hospital";
    }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> baseActions = new ArrayList<>(Arrays.asList());
        
        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            baseActions.add(ActionFactory.create(ActionType.WORK));
        }
        
        baseActions.add(ActionFactory.create(ActionType.GET_CHECKUP));
        baseActions.add(ActionFactory.create(ActionType.NAP));
        baseActions.add(ActionFactory.create(ActionType.USE_TOILET));
        
        return baseActions;
    }

    private boolean canWorkHere(Sim sim) {
        for (String location : sim.getJob().getWorkLocations()) {
            if (this.key().equalsIgnoreCase(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String onEnter(Sim sim) {
        return sim.getName() + " entered the hospital. Quiet and clean.";
    }
}
