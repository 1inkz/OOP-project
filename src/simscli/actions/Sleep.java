package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class Sleep implements Action {
    @Override public String name() { return "Sleep"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.ENERGY, +45)
                .plus(NeedType.HUNGER, -6)
                .plus(NeedType.BLADDER, -6)
                .plus(NeedType.FUN, +2));
        return sim.getName() + " slept deeply.";
    }
}