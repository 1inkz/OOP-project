package simscli.policy;

import java.util.List;
import simscli.game.GameClock;
import simscli.game.Game;
import simscli.game.GameLogger;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * At night (23:00-07:59), all Sims lose extra energy.
 */
public final class MidnightEnergyDrainPolicy implements TimeRulePolicy {
    @Override
    public void apply(GameClock clock, List<Sim> sims, GameLogger logger, Game game) {
        int currentHour = clock.getHour();
        if (currentHour < 23 && currentHour > 7) {
            return;
        }

        logger.warn("\n[GAME] It's Midnight - Sim's energy draining fast!");
        for (Sim sim : sims) {
            sim.applyEffect(Effect.none().plus(NeedType.ENERGY, -10));
        }
    }
}
