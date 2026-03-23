package simscli.location;

import simscli.actions.*;
import simscli.actions.interactive.*;
import simscli.sims.Sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

        // ===== SECTION: HUMAN ACTIONS =====
        actions.addAll(Arrays.asList(
        		ActionFactory.create(ActionType.EAT_SNACK),
        		ActionFactory.create(ActionType.EAT_MEAL),
        		ActionFactory.create(ActionType.BRUSH_TEETH),
        		ActionFactory.create(ActionType.USE_TOILET),
        		ActionFactory.create(ActionType.SHOWER),
        		ActionFactory.create(ActionType.NAP),
        		(isAfter7PM(sim.getGame().getClock().getHour()) ? ActionFactory.create(ActionType.SLEEP) : null),                          
                ActionFactory.create(ActionType.WATCH_TV),
                ActionFactory.create(ActionType.PLAY_GAME),
                ActionFactory.create(ActionType.READ_BOOK)        
        ));
        
        // ===== SECTION: PET ACTIONS =====
        if (sim != null && !sim.getPets().isEmpty()) {
            List<Action> petActions = new ArrayList<>(Arrays.asList(
                    new FeedPetMenu(),
                    new ShowerPetMenu(),
                    new PlayWithPetMenu()
            ));

            if (isAfter7PM(sim.getGame().getClock().getHour())) {
                petActions.add(new SleepWithPetMenu());
            }
            actions.addAll(petActions);
        }
        
        actions.removeIf(Objects::isNull);
        return actions;
    }

	private boolean isAfter7PM(int hour) {
	    return hour >= 19 || hour < 8;
	}
}