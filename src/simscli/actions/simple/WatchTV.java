package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class WatchTV implements Action {
    @Override public String name() { return "Watch TV"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +28)
                .plus(NeedType.SOCIAL, -1)
                .plus(NeedType.ENERGY, -2)
                .plus(NeedType.BLADDER, -2));

        sim.gainSkill(SkillType.CREATIVITY, 2);

        return sim.getName() + " watched TV and picked up a little Creativity.";
    }
}
