package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class ReadBook implements Action {
    @Override public String name() { return "Read Book"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +18)
                .plus(NeedType.SOCIAL, -1)
                .plus(NeedType.ENERGY, -2));

        sim.gainSkill(SkillType.INTELLIGENCE, 5);
        sim.gainSkill(SkillType.CREATIVITY, 2);

        return sim.getName() + " read a book and improved Intelligence.";
    }
}
