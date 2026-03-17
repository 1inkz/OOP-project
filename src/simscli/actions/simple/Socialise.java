package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class Socialise implements Action {
    @Override public String name() { return "Socialise"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.SOCIAL, +35)
                .plus(NeedType.FUN, +10)
                .plus(NeedType.ENERGY, -4)
                .plus(NeedType.HUNGER, -3)
                .plus(NeedType.BLADDER, -3));

        sim.gainSkill(SkillType.CHARISMA, 6);

        return sim.getName() + " socialised and improved Charisma.";
    }
}
