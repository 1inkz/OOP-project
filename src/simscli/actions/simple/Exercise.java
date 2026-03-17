package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class Exercise implements Action {
    @Override public String name() { return "Exercise"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.ENERGY, -10)
                .plus(NeedType.HUNGER, -8)
                .plus(NeedType.HYGIENE, -6)
                .plus(NeedType.FUN, +6)
                .plus(NeedType.SOCIAL, -1)
                .plus(NeedType.BLADDER, -3));

        sim.gainSkill(SkillType.FITNESS, 6);
        sim.gainSkill(SkillType.WORK_ETHIC, 1);

        return sim.getName() + " exercised and improved Fitness.";
    }
}
