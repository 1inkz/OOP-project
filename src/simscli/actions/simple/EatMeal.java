package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

/**
 * Action: Eat a proper meal, restoring hunger and improving cooking.
 */
public final class EatMeal implements Action {
    @Override public String name() { return "Eat Meal"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HUNGER, +35)
                .plus(NeedType.ENERGY, +15)
                .plus(NeedType.HYGIENE, -2)
                .plus(NeedType.BLADDER, -4)
                .plus(NeedType.FUN, +3));

        sim.gainSkill(SkillType.COOKING, 4);

        return sim.getName() + " ate a proper meal and improved Cooking.";
    }
}
