package simscli;

import org.junit.After;
import org.junit.Test;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.SimType;

import static org.junit.Assert.*;

public class JoblessBehaviorTest {

    private final Game g = new Game();

    @After
    public void tearDown() {
        g.shutdown(); // important: avoid thread pool leak
    }

    @Test
    public void newSimStartsJobless() {
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);
        assertEquals("Jobless", g.activeSim().getJobName());
    }

    @Test
    public void joblessCannotWork_moneyUnchanged() {
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int before = g.activeSim().getSimcoin();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));
        int after = g.activeSim().getSimcoin();

        assertEquals(before, after);
        assertTrue(msg.toLowerCase().contains("jobless"));
    }
}