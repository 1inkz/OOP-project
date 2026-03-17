package simscli;

import simscli.game.Game;
import simscli.sims.SimType;

public class LocationTravelTest {

    public static void travelChangesLocation() {
        Game g = new Game();
        g.createSim("Kai", SimType.ADULT);
        g.setActiveSim(0);

        String before = g.activeSim().getLocation().key();
        g.travelTo("park");
        String after = g.activeSim().getLocation().key();

        assert !before.equals(after);
        assert after.equals("park");
        System.out.println("✓ travelChangesLocation");
        g.shutdown();
    }

    public static void main(String[] args) {
        travelChangesLocation();
    }
}