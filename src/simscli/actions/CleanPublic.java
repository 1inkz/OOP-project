package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class CleanPublic implements Action {
    @Override public String name() { return "Cleaning in Public"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +15)
                .plus(NeedType.ENERGY, -3)
        		.plus(NeedType.BLADDER, +15));
        return sim.getName() + " cleaning in public";
    }
}