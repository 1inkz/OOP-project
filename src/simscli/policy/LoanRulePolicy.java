package simscli.policy;

import java.util.List;
import simscli.game.Game;
import simscli.game.GameLogger;
import simscli.sims.Sim;

/**
 * Policy contract for applying loan-related consequences.
 */
public interface LoanRulePolicy {
    void apply(List<Sim> sims, Sim activeSim, Game game, GameLogger logger);
}
