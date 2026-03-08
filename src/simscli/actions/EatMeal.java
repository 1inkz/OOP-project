package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class EatMeal implements Action {
    @Override public String name() { return "Eat Meal"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HUNGER, +35)
                .plus(NeedType.ENERGY, +15)
                .plus(NeedType.HYGIENE, -2)
                .plus(NeedType.BLADDER, -4)
                .plus(NeedType.FUN, +2));
        return sim.getName() + " ate a proper meal.";
    }
}