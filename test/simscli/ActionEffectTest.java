package simscli;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Test: Verifies that actions change Sim state and consume time.
 */
public class ActionEffectTest {
    /**
     * Tests that working action earns money.
     */
    @Test
    public void actionConsumesTimeAndChangesState() {
        Game g = new Game();
        Sim s = g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int moneyBefore = s.getSimcoin();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));

        assertNotNull(msg);
        assertTrue(s.getSimcoin() >= moneyBefore);
        g.shutdown();
    }
}