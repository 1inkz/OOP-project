package simscli;

import org.junit.Test;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;

import static org.junit.Assert.*;

public class ActionEffectTest {
    @Test
    public void actionConsumesTimeAndChangesState() {
        Game g = new Game();
        Sim s = g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int moneyBefore = s.getMoney();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));

        assertNotNull(msg);
        assertTrue(s.getMoney() > moneyBefore);
        g.shutdown();
    }
}