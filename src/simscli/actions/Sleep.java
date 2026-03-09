package simscli.actions;


import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class Sleep implements Action {
	 
    @Override public String name() { return "Sleep"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
    	ctx.checkTimeRules();
        return sim.getName() + " slept deeply. . Woke up the next day!";
    }
}