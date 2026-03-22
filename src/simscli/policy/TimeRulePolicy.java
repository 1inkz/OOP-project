package simscli.policy;

import java.util.List;
import simscli.game.GameClock;
import simscli.game.GameLogger;
import simscli.game.Game;
import simscli.sims.Sim;

/**
 * Policy contract for time-based world rules.
 */
public interface TimeRulePolicy {
    void apply(GameClock clock, List<Sim> sims, GameLogger logger, Game game);
}
