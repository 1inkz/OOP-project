package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class Shower implements Action {
    @Override public String name() { return "Shower"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +40)
                .plus(NeedType.ENERGY, -3)
                .plus(NeedType.SOCIAL, +2));
        return sim.getName() + " showered and feels fresh.";
    }
}
