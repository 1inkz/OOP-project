package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class Nap implements Action {
    @Override public String name() { return "Nap"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.ENERGY, +30)
                .plus(NeedType.HUNGER, -3)
                .plus(NeedType.BLADDER, -3));
        return sim.getName() + " took a nap.";
    }
}
