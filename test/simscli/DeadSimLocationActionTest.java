package simscli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Regression test: dead Sims must not be able to execute location actions.
 */
public class DeadSimLocationActionTest {

    @Test
    public void deadSimCannotDeepSleepAtLocation() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        game.travelTo("park");

        // Force death through needs effect the same way gameplay effects are applied.
        sim.applyEffect(Effect.none().plus(NeedType.ENERGY, -200));

        String msg = game.performLocationAction(4); // Park action index 4 = Deep Sleep
        assertTrue(msg.toLowerCase().contains("no longer in the simulation"));

        game.shutdown();
    }
}
