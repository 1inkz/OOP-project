package simscli.location;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    	List<Action> baseActions = new ArrayList<>(Arrays.asList(
                ActionFactory.create(ActionType.NAP),
                ActionFactory.create(ActionType.USE_TOILET)
        ));
    	
        if (sim != null && sim.getJob().canWork() 
                && sim.getJob().getWorkLocation().equals(this.key())) {
            baseActions.add(ActionFactory.create(ActionType.WORK));
        }
        return baseActions;
    }

    @Override
    public String onEnter(Sim sim) {
        return sim.getName() + " entered the hospital. Quiet and clean.";
    }
}