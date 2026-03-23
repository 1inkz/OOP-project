package simscli.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        List<Action> actions = new ArrayList<>(Arrays.asList());
        
        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            actions.add(ActionFactory.create(ActionType.WORK));
        }
        
        // ===== SECTION: HUMAN ACTIONS =====
        actions.addAll(Arrays.asList(
        		ActionFactory.create(ActionType.EAT_SNACK),
        		ActionFactory.create(ActionType.USE_TOILET),
        		ActionFactory.create(ActionType.CLEAN_PUBLIC),
        		ActionFactory.create(ActionType.NAP),
        		(isAfter7PM(sim.getGame().getClock().getHour()) ? ActionFactory.create(ActionType.SLEEP) : null),                          
                ActionFactory.create(ActionType.SOCIALISE),
                ActionFactory.create(ActionType.EXERCISE)        
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

        // Add pet menu if Sim has pets
        if (sim != null && !sim.getPets().isEmpty()) {
            actions.add(new PlayWithPetMenu());
        }
      
        actions.removeIf(Objects::isNull);
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
    
	private boolean isAfter7PM(int hour) {
	    return hour >= 19 || hour < 8;
	}
}
