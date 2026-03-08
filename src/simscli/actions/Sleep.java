package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class Sleep implements Action {
	
    @Override public String name() { return "Sleep"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
    	ctx.resetToNextDayMorning();
        sim.applyEffect(Effect.none()
                .plus(NeedType.ENERGY, 90)
                .plus(NeedType.HUNGER, 30)
                .plus(NeedType.BLADDER, -3));
        return sim.getName() + " slept deeply. . Woke up the next day!";
    }
}