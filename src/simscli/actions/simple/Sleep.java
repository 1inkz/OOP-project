package simscli.actions.simple;


import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;

public final class Sleep implements Action {
	 
    @Override public String name() { return "Deep Sleep"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
    	ctx.checkTimeRules();
        return sim.getName() + " slept deeply. . Woke up the next day!";
    }
}
