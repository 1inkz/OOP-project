package simscli.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import simscli.actions.*;
import simscli.actions.interactive.*;
import simscli.sims.Sim;

/**
 * Park location: offers outdoor recreation and work opportunities.
 */
public final class Park extends Location {
    @Override public String key() { return "park"; }
    @Override public String name() { return "Park"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>(Arrays.asList(
                ActionFactory.create(ActionType.SOCIALISE),
                ActionFactory.create(ActionType.EXERCISE),
                ActionFactory.create(ActionType.EAT_SNACK),
                ActionFactory.create(ActionType.NAP),
                ActionFactory.create(ActionType.SLEEP),
                ActionFactory.create(ActionType.CLEAN_PUBLIC)
        ));

        // Add pet menu if Sim has pets
        if (sim != null && !sim.getPets().isEmpty()) {
            actions.add(new PlayWithPetMenu());
        }

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
