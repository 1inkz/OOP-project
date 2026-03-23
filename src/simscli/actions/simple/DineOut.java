package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Action: Dining in restaurant
 */
public final class DineOut implements Action {
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
