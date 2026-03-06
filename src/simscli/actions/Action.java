package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;

/** Command pattern: each action knows how to apply itself to the Sim. */
public interface Action {
    String name();
    String perform(Sim sim, GameContext ctx);
}