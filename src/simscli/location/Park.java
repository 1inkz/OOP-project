package simscli.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import simscli.actions.*;
import simscli.sims.Sim;

public final class Park extends Location {
    @Override public String key() { return "park"; }
    @Override public String name() { return "Park"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>(Arrays.asList(
                ActionFactory.create(ActionType.SOCIALISE),
                ActionFactory.create(ActionType.EXERCISE),
                ActionFactory.create(ActionType.NAP),
                ActionFactory.create(ActionType.SLEEP),
                ActionFactory.create(ActionType.CLEAN_PUBLIC)
        ));

        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            actions.add(ActionFactory.create(ActionType.WORK));
        }

        return actions;
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
