package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

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
        return sim.getName() + " socialised. Drama avoided... this time.";
    }
}