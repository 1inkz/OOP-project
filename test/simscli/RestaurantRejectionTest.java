package simscli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.sims.SimType;

/**
 * Test: Verifies restaurant dining requires sufficient funds.
 */
public class RestaurantRejectionTest {

    /**
     * Tests that dining out fails without 25 Simcoin.
     */
    @Test
    public void dineOutRejectedIfInsufficientMoney() {
        Game g = new Game();
        g.createSim("Mia", SimType.ADULT);
        g.setActiveSim(0);

        // Ensure she CAN go restaurant
        g.travelTo("restaurant");

        // Make sure money is low
        // If you don't have a setter, just spend until low (safe approach)
        while (g.activeSim().getSimcoin() > 0) {
            try {
                g.activeSim().spendSimcoin(1);
            } catch (Exception ex) {
                break;
            }
        }

        // Now perform first location action (assumes Restaurant actions list starts with Dine Out)
        // If your Restaurant action order differs, adjust index.
        String msg = g.performLocationAction(0);

        assertTrue(msg.toLowerCase().contains("can't afford") || msg.toLowerCase().contains("need $"));
        g.shutdown();
    }
}