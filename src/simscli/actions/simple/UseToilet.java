package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class UseToilet implements Action {
    @Override public String name() { return "Use Toilet"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.BLADDER, +45)
                .plus(NeedType.HYGIENE, -2));
        return sim.getName() + " used the toilet. Relief.";
    }
}
