package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.actions.request.AmountActionRequest;
import simscli.game.Game;
import simscli.location.Location;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Verifies casino travel and gambling behavior.
 */
public class CasinoGameplayTest {

    /**
     * Traveling to casino should update the active Sim location.
     */
    @Test
    public void travelToCasinoShouldChangeLocation() {
        Game g = new Game();
        g.createSim("Rin", SimType.ADULT);
        g.setActiveSim(0);

        String msg = g.travelTo("casino");

        assertEquals("casino", g.activeSim().getLocation().key());
        assertTrue(msg.toLowerCase().contains("casino"));
        g.shutdown();
    }

    /**
     * Slot machine rejects invalid request bet without changing money.
     */
    @Test
    public void slotMachineShouldRejectInvalidBet() {
        Game g = new Game();
        Sim sim = g.createSim("Ari", SimType.ADULT);
        g.setActiveSim(0);
        g.travelTo("casino");

        int before = sim.getSimcoin();
        String msg = g.performLocationAction(0, new AmountActionRequest(before + 1));

        assertTrue(msg.toLowerCase().contains("invalid bet"));
        assertEquals(before, sim.getSimcoin());
        g.shutdown();
    }

    /**
     * Gambling results stay within valid bankroll bounds.
     */
    @Test
    public void casinoGamesShouldKeepMoneyWithinExpectedBounds() {
        Game g = new Game();
        Sim sim = g.createSim("Nia", SimType.ADULT);
        g.setActiveSim(0);
        g.travelTo("casino");

        int initial = sim.getSimcoin();

        String slotMsg = g.performLocationAction(0, new AmountActionRequest(100));
        int afterSlots = sim.getSimcoin();
        assertTrue(slotMsg.toLowerCase().contains("slots"));
        assertTrue(afterSlots >= initial - 100 && afterSlots <= initial + 2400);

        int betForBlackjack = Math.min(100, sim.getSimcoin());
        String blackjackMsg = g.performLocationAction(1, new AmountActionRequest(betForBlackjack));
        int afterBlackjack = sim.getSimcoin();
        assertTrue(blackjackMsg.toLowerCase().contains("blackjack") || blackjackMsg.toLowerCase().contains("fold"));
        assertTrue(afterBlackjack >= 0);

        g.shutdown();
    }

    /**
     * Children should not be allowed to enter casino.
     */
    @Test
    public void childShouldNotSeeOrEnterCasino() {
        Game g = new Game();
        Sim child = g.createSim("Tim", SimType.CHILD);
        g.setActiveSim(0);

        Location casino = g.location().get("casino");
        assertTrue(casino != null && !casino.canEnter(child));

        String msg = g.travelTo("casino");
        assertTrue(msg.toLowerCase().contains("cannot enter"));
        assertEquals("street", g.activeSim().getLocation().key());

        g.shutdown();
    }

    /**
     * Casino should include basic needs actions in addition to gambling.
     */
    @Test
    public void casinoShouldOfferSnackAndToiletActions() {
        Game g = new Game();
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);
        g.travelTo("casino");

        String snackMsg = g.performLocationAction(3);
        String toiletMsg = g.performLocationAction(2);

        assertTrue(snackMsg.toLowerCase().contains("snack") || snackMsg.toLowerCase().contains("ate"));
        assertTrue(toiletMsg.toLowerCase().contains("toilet") || toiletMsg.toLowerCase().contains("used"));

        g.shutdown();
    }
}
