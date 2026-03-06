package simscli.location;

import simscli.actions.*;

import java.util.Arrays;
import java.util.List;

public final class Park extends Location {
    @Override public String key() { return "park"; }
    @Override public String name() { return "Park"; }

    @Override
    public List<Action> actions() {
        return Arrays.asList(
                ActionFactory.create(ActionType.SOCIALISE),
                ActionFactory.create(ActionType.EXERCISE),
                ActionFactory.create(ActionType.NAP) // nap on bench lol
        );
    }
}