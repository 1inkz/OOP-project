package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Action: Brush teeth for minor hygiene improvement.
 */
public final class BrushTeeth implements Action {
    @Override public String name() { return "Brush Teeth"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +15)
                .plus(NeedType.FUN, -1));
        return sim.getName() + " brushed teeth. Minty.";
    }
}
