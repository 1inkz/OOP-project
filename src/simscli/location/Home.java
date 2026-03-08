package simscli.location;

import simscli.actions.*;
import simscli.sims.Sim;

import java.util.Arrays;
import java.util.List;

public final class Home extends Location {
    @Override public String key() { return "home"; }
    @Override public String name() { return "Home"; }

    @Override
    public List<Action> actions(Sim sim) {
        return Arrays.asList(
                ActionFactory.create(ActionType.EAT_MEAL),
                ActionFactory.create(ActionType.EAT_SNACK),
                ActionFactory.create(ActionType.SLEEP),
                ActionFactory.create(ActionType.NAP),
                ActionFactory.create(ActionType.SHOWER),
                ActionFactory.create(ActionType.BRUSH_TEETH),
                ActionFactory.create(ActionType.USE_TOILET),
                ActionFactory.create(ActionType.WATCH_TV),
                ActionFactory.create(ActionType.PLAY_GAME),
                ActionFactory.create(ActionType.READ_BOOK)
        );
    }
}