package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class PlayGame implements Action {
    @Override public String name() { return "Play Game"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +35)
                .plus(NeedType.SOCIAL, -2)
                .plus(NeedType.ENERGY, -4)
                .plus(NeedType.BLADDER, -4));
        return sim.getName() + " played games and yelled at pixels.";
    }
}