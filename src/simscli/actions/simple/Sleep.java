package simscli.actions.simple;


import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.NeedType;

/**
 * Action: Sleep deeply and advance to the next day.
 */
public final class Sleep implements Action {
	 
    @Override public String name() { return "Deep Sleep"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
    	ctx.resetToNextDayMorning();
    	sim.settleBankInterest();
    	sim.getNeeds().set(NeedType.ENERGY, 90);
        sim.getNeeds().set(NeedType.HUNGER, 30);
        return sim.getName() + " slept deeply. . Woke up the next day!";
    }
}
