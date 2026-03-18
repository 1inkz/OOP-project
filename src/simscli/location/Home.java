package simscli.location;

import simscli.actions.*;
import simscli.actions.interactive.*;
import simscli.sims.Sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Home location: requires owned house, offers residential actions and pet care.
 */
public final class Home extends Location {
    @Override public String key() { return "home"; }
    @Override public String name() { return "Home"; }

    @Override
    public boolean canEnter(Sim sim) {
        return sim != null && sim.isAlive() && sim.getOwnedHouse() != null;
    }
    
    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>();

        // ===== SECTION: PET ACTIONS =====
        if (sim != null && !sim.getPets().isEmpty()) {
            actions.addAll(Arrays.asList(
                    new FeedPetMenu(),
                    new ShowerPetMenu(),
                    new PlayWithPetMenu(),
                    new SleepWithPetMenu()
            ));
        }

        // ===== SECTION: HUMAN ACTIONS =====
        actions.addAll(Arrays.asList(
                ActionFactory.create(ActionType.USE_TOILET),
                ActionFactory.create(ActionType.SLEEP),
                ActionFactory.create(ActionType.WATCH_TV),
                ActionFactory.create(ActionType.PLAY_GAME),
                ActionFactory.create(ActionType.READ_BOOK),
                ActionFactory.create(ActionType.EAT_MEAL),
                ActionFactory.create(ActionType.SHOWER)
        ));

        return actions;
    }
}