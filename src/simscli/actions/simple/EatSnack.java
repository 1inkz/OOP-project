package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class EatSnack implements Action {
    @Override public String name() { return "Eat Snack"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HUNGER, +18)
                .plus(NeedType.ENERGY, +4)
                .plus(NeedType.HYGIENE, -1)
                .plus(NeedType.BLADDER, -2)
                .plus(NeedType.FUN, +2));

        sim.gainSkill(SkillType.COOKING, 1);

        return sim.getName() + " ate a little snack.";
    }
}
