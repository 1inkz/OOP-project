package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.SimType;

/**
 * Test: Verifies jobless Sims cannot work or earn.
 */
public class JoblessBehaviorTest {

    /**
     * Tests that new Sims start unemployed.
     */
    @Test
    public void newSimStartsJobless() {
        Game g = new Game();
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);
        assertEquals("Jobless", g.activeSim().getJobName());
        g.shutdown();
    }

    /**
     * Tests that jobless Sims cannot work or earn.
     */
    @Test
    public void joblessCannotWork_moneyUnchanged() {
        Game g = new Game();
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int before = g.activeSim().getSimcoin();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));
        int after = g.activeSim().getSimcoin();

        assertEquals(before, after);
        assertTrue(msg.toLowerCase().contains("jobless"));
        g.shutdown();
    }
}