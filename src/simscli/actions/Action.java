package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.actions.request.ActionRequest;

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

    /**
     * Executes this action with an optional request payload.
     *
     * Default behavior keeps backward compatibility by delegating
     * to the original perform method for actions that are purely interactive.
     */
    default String perform(Sim sim, GameContext ctx, ActionRequest request) {
        return perform(sim, ctx);
    }
}