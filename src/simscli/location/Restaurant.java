package simscli.location;

import java.util.*;
import simscli.actions.*;
import simscli.sims.Sim;

/**
 * Restaurant location: dining and Chef employment.
 */
public final class Restaurant extends Location {
    @Override public String key() { return "restaurant"; }
    @Override public String name() { return "Restaurant"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> baseActions = new ArrayList<>(Arrays.asList());
        
        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            baseActions.add(ActionFactory.create(ActionType.WORK));
        }
        
        baseActions.add(ActionFactory.create(ActionType.DINE_OUT));
        baseActions.add(ActionFactory.create(ActionType.EAT_SNACK));
        baseActions.add(ActionFactory.create(ActionType.USE_TOILET));
        baseActions.add(ActionFactory.create(ActionType.SOCIALISE));

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


}
