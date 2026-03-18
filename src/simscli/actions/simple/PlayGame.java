package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

/**
 * Action: Play games for fun and gaming skill.
 */
public final class PlayGame implements Action {
    @Override public String name() { return "Play Game"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +35)
                .plus(NeedType.SOCIAL, -2)
                .plus(NeedType.ENERGY, -4)
                .plus(NeedType.BLADDER, -4));

        sim.gainSkill(SkillType.GAMING, 6);

        return sim.getName() + " played games and improved Gaming.";
    }
}
