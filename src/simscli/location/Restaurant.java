package simscli.location;

import simscli.actions.*;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.*;

import java.util.*;

public final class Restaurant extends Location {
    @Override public String key() { return "restaurant"; }
    @Override public String name() { return "Restaurant"; }

    @Override
    public List<Action> actions(Sim sim) {
    	 List<Action> baseActions = new ArrayList<>(Arrays.asList(
                new DineOut(),      // special action only here
                new Socialise()     // reuse existing action
        ));
    	 
         if (sim != null && sim.getJob().canWork() 
                 && sim.getJob().getWorkLocation().equals(this.key())) {
             baseActions.add(ActionFactory.create(ActionType.WORK));
         }
         return baseActions;
    }

    /** Location-specific action: costs money, boosts hunger + social + fun. */
    private static final class DineOut implements Action {
        @Override public String name() { return "Dine Out ($25)"; }

        @Override
        public String perform(Sim sim, GameContext ctx) {
            if (sim.getSimcoin() < 25) {
                return sim.getName() + " can't afford to dine out. (Need $25)";
            }

            // Pay money (controlled by Sim method)
            sim.spendSimcoin(25);
            // We *must* manipulate via method where needed: add a spend method in Sim (see changes below)
            // For now this will call sim.spendMoney(25) once you add it.
            // (You will add spendMoney in Sim in the change section.)

            sim.applyEffect(Effect.none()
                    .plus(NeedType.HUNGER, +45)
                    .plus(NeedType.ENERGY, +30)
                    .plus(NeedType.SOCIAL, +15)
                    .plus(NeedType.FUN, +10)
                    .plus(NeedType.HYGIENE, -2)
                    .plus(NeedType.BLADDER, -4));

            return sim.getName() + " dined out. Fancy food, fancy bill.";
        }
    }
}