package simscli;

import org.junit.After;
import org.junit.Test;
import simscli.game.Game;
import simscli.sims.SimType;

import static org.junit.Assert.*;

public class LocationTravelTest {

    private final Game g = new Game();

    @After
    public void tearDown() {
        g.shutdown();
    }

    @Test
    public void travelChangesLocation() {
        g.createSim("Kai", SimType.ADULT);
        g.setActiveSim(0);

        String before = g.activeSim().getLocation().key();
        g.travelTo("park");
        String after = g.activeSim().getLocation().key();

        assertNotEquals(before, after);
        assertEquals("park", after);
    }
}