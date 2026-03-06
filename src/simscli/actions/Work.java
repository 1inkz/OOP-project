package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;

public final class Work implements Action {
    @Override public String name() { return "Work"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        return sim.work();
    }
}