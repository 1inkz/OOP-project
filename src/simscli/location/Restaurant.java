package simscli.location;

import java.util.*;
import simscli.actions.*;
import simscli.actions.simple.*;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.*;

/**
 * Restaurant location: dining and Chef employment.
 */
public final class Restaurant extends Location {
    @Override public String key() { return "restaurant"; }
    @Override public String name() { return "Restaurant"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> baseActions = new ArrayList<>(Arrays.asList(
                new DineOut(),
                new Socialise()
        ));

        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            baseActions.add(ActionFactory.create(ActionType.WORK));
        }

        return baseActions;
    }

    private boolean canWorkHere(Sim sim) {
        for (String location : sim.getJob().getWorkLocations()) {
            if (this.key().equalsIgnoreCase(location)) {
                return true;
            }
        }
        return false;
    }

    private static final class DineOut implements Action {
        @Override public String name() { return "Dine Out ($25)"; }

        @Override
        public String perform(Sim sim, GameContext ctx) {
            if (sim.getSimcoin() < 25) {
                return sim.getName() + " can't afford to dine out. (Need $25)";
            }

            sim.spendSimcoin(25);

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
