package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class Cleaning implements Action {
    @Override public String name() { return "Cleaning the area"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +15)
                .plus(NeedType.ENERGY, -3)
                .plus(NeedType.BLADDER, +15));

        sim.gainSkill(SkillType.CLEANING, 5);
        sim.gainSkill(SkillType.WORK_ETHIC, 1);

        return sim.getName() + " cleaned in public and improved Cleaning.";
    }
}
