package simscli.location;

import simscli.actions.*;
import simscli.sims.Sim;

import java.util.Arrays;
import java.util.List;

public final class Park extends Location {
    @Override public String key() { return "park"; }
    @Override public String name() { return "Park"; }

    @Override
    public List<Action> actions(Sim sim) {
        return Arrays.asList(
                ActionFactory.create(ActionType.SOCIALISE),
                ActionFactory.create(ActionType.EXERCISE),
                ActionFactory.create(ActionType.NAP), // nap on bench lol
                ActionFactory.create(ActionType.SLEEP), // nap on bench lol
                ActionFactory.create(ActionType.CLEAN_PUBLIC)
        );
    }
    
}