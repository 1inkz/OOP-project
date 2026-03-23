package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Action: Clean public areas for cleaning skill improvement.
 */
public final class Cleaning implements Action {
    @Override public String name() { return "Cleaning in Public"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +15)
                .plus(NeedType.ENERGY, -3)
                .plus(NeedType.BLADDER, +15));

        return sim.getName() + " cleaned in public. Hygiene UP!";
    }
}
