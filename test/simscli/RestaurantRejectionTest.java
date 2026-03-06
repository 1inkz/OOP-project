package simscli;

import org.junit.After;
import org.junit.Test;
import simscli.game.Game;
import simscli.jobs.JobFactory;
import simscli.sims.SimType;

import static org.junit.Assert.*;

public class RestaurantRejectionTest {

    private final Game g = new Game();

    @After
    public void tearDown() {
        g.shutdown();
    }

    @Test
    public void dineOutRejectedIfInsufficientMoney() {
        g.createSim("Mia", SimType.ADULT);
        g.setActiveSim(0);

        // Ensure she CAN go restaurant
        g.travelTo("restaurant");

        // Make sure money is low
        // If you don't have a setter, just spend until low (safe approach)
        while (g.activeSim().getMoney() > 0) {
            try {
                g.activeSim().spendMoney(1);
            } catch (Exception ex) {
                break;
            }
        }

        // Now perform first location action (assumes Restaurant actions list starts with Dine Out)
        // If your Restaurant action order differs, adjust index.
        String msg = g.performLocationAction(0);

        assertTrue(msg.toLowerCase().contains("can't afford") || msg.toLowerCase().contains("need $"));
    }
}