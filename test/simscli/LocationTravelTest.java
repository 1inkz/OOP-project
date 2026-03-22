package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
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
}