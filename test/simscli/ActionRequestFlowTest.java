package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.actions.request.AmountActionRequest;
import simscli.actions.request.BuyPetActionRequest;
import simscli.game.Game;
import simscli.location.LocationKey;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Verifies request-object execution paths for actions without direct UI input.
 */
public class ActionRequestFlowTest {

    @Test
    public void depositAndWithdrawUseAmountRequest() {
        Game g = new Game();
        Sim sim = g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        String depositMsg = g.performAction(ActionFactory.create(ActionType.DEPOSIT), new AmountActionRequest(100));
        assertTrue(depositMsg.toLowerCase().contains("deposited"));
        assertEquals(200, sim.getSimcoin());
        assertEquals(100, sim.getBankDeposit());

        String withdrawMsg = g.performAction(ActionFactory.create(ActionType.WITHDRAW), new AmountActionRequest(40));
        assertTrue(withdrawMsg.toLowerCase().contains("withdrew"));
        assertEquals(240, sim.getSimcoin());
        assertEquals(60, sim.getBankDeposit());

        g.shutdown();
    }

    @Test
    public void buyPetUsesRequestObject() {
        Game g = new Game();
        Sim sim = g.createSim("Kai", SimType.ADULT);
        g.setActiveSim(0);

        g.travelTo(LocationKey.PETSTORE);

        String msg = g.performAction(
                ActionFactory.create(ActionType.BUY_PET),
                new BuyPetActionRequest(1, "Milo"));

        assertTrue(msg.toLowerCase().contains("bought"));
        assertEquals(1, sim.getPets().size());

        g.shutdown();
    }
}
