package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;

/**
 * Interface for all in-game actions.
 * Implements Command pattern: actions know all dependencies to execute themselves.
 */
public interface Action {
    /**
     * Gets the display name of this action.
     * @return action name for UI
     */
    String name();
    /**
     * Executes this action on the specified Sim.
     * @param sim the Sim performing the action
     * @param ctx the game context with UI and state access
     * @return message describing action outcome
     */
    String perform(Sim sim, GameContext ctx);
}