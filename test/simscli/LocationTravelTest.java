package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.actions.request.AmountActionRequest;
import simscli.game.Game;
import simscli.location.Bank;
import simscli.sims.SimType;

/**
 * Test: Verifies Sim location changes with travel.
 */
public class LocationTravelTest {

    /**
     * Tests that traveling to park changes location.
     */
    @Test
    public void travelChangesLocation() {
        Game g = new Game();
        g.createSim("Kai", SimType.ADULT);
        g.setActiveSim(0);

        String before = g.activeSim().getLocation().key();
        g.travelTo("park");
        String after = g.activeSim().getLocation().key();

        assertNotEquals(before, after);
        assertEquals("park", after);
        g.shutdown();
    }

    @Test
    public void childWithMoneyCanDepositAndWithdrawFromBank() {
        Game g = new Game();
        g.createSim("Mia", SimType.CHILD);
        g.setActiveSim(0);

        Bank bank = new Bank();
        List<Action> initialActions = bank.actions(g.activeSim());
        assertTrue(hasAction(initialActions, "Deposit Simcoin"));
        assertFalse(hasAction(initialActions, "Withdraw Simcoin"));

        String depositMsg = g.performAction(ActionFactory.create(ActionType.DEPOSIT), new AmountActionRequest(100));
        assertTrue(depositMsg.toLowerCase().contains("deposited"));

        List<Action> postDepositActions = bank.actions(g.activeSim());
        assertTrue(hasAction(postDepositActions, "Withdraw Simcoin"));

        g.shutdown();
    }

    @Test
    public void simWithoutMoneyCannotSeeDepositAction() {
        Game g = new Game();
        g.createSim("Liam", SimType.ADULT);
        g.setActiveSim(0);

        g.activeSim().spendSimcoin(g.activeSim().getSimcoin());

        Bank bank = new Bank();
        List<Action> actions = bank.actions(g.activeSim());
        assertFalse(hasAction(actions, "Deposit Simcoin"));

        g.shutdown();
    }

    private boolean hasAction(List<Action> actions, String actionName) {
        return actions.stream().anyMatch(action -> actionName.equals(action.name()));
    }
}