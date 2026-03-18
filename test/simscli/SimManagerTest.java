package simscli;

import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Test: Verifies Sim creation, activation, and lifecycle management.
 */
public class SimManagerTest {
    
    public static void testCreateAdultSim() {
        Game game = new Game();
        Sim sim = game.createSim("Alice", SimType.ADULT);
        
        assert sim != null;
        assert sim.getName().equals("Alice");
        assert sim.getType() == SimType.ADULT;
        game.shutdown();
        System.out.println("✓ testCreateAdultSim");
    }

    public static void testCreateChildSim() {
        Game game = new Game();
        Sim sim = game.createSim("Bobby", SimType.CHILD);
        
        assert sim != null;
        assert sim.getName().equals("Bobby");
        assert sim.getType() == SimType.CHILD;
        game.shutdown();
        System.out.println("✓ testCreateChildSim");
    }

    public static void testGetActiveSim() {
        Game game = new Game();
        Sim sim1 = game.createSim("Sim1", SimType.ADULT);
        
        Sim active = game.activeSim();
        assert active != null;
        assert active.getName().equals("Sim1");
        game.shutdown();
        System.out.println("✓ testGetActiveSim");
    }

    public static void testSetActiveSim() {
        Game game = new Game();
        Sim sim1 = game.createSim("Sim1", SimType.ADULT);
        Sim sim2 = game.createSim("Sim2", SimType.ADULT);
        
        game.setActiveSim(0);
        assert game.activeSim().getName().equals("Sim1");
        
        game.setActiveSim(1);
        assert game.activeSim().getName().equals("Sim2");
        game.shutdown();
        System.out.println("✓ testSetActiveSim");
    }

    public static void testMultipleSims() {
        Game game = new Game();
        Sim sim1 = game.createSim("Sim1", SimType.ADULT);
        Sim sim2 = game.createSim("Sim2", SimType.CHILD);
        Sim sim3 = game.createSim("Sim3", SimType.ELDER);
        
        assert game.getAllSims().size() == 3;
        game.shutdown();
        System.out.println("✓ testMultipleSims");
    }

    public static void testSimStartsAtStreet() {
        Game game = new Game();
        Sim sim = game.createSim("TestSim", SimType.ADULT);
        
        assert sim.getLocation() != null;
        assert sim.getLocation().key().equals("street");
        game.shutdown();
        System.out.println("✓ testSimStartsAtStreet");
    }

    public static void testSimStartsWithJobless() {
        Game game = new Game();
        Sim sim = game.createSim("TestSim", SimType.ADULT);
        
        assert sim.getJob() != null;
        assert sim.getJob().name().equals("jobless");
        game.shutdown();
        System.out.println("✓ testSimStartsWithJobless");
    }

    public static void testSimIsAliveFresh() {
        Game game = new Game();
        Sim sim = game.createSim("TestSim", SimType.ADULT);
        assert sim.isAlive();
        game.shutdown();
        System.out.println("✓ testSimIsAliveFresh");
    }

    public static void testSimHasNeeds() {
        Game game = new Game();
        Sim sim = game.createSim("TestSim", SimType.ADULT);
        
        assert sim.getNeeds() != null;
        assert sim.getNeeds().get(simscli.stats.NeedType.HUNGER) > 0;
        game.shutdown();
        System.out.println("✓ testSimHasNeeds");
    }

    public static void testSimCount() {
        Game game = new Game();
        assert game.getAllSims().size() == 0;
        
        game.createSim("Sim1", SimType.ADULT);
        assert game.getAllSims().size() == 1;
        
        game.createSim("Sim2", SimType.ADULT);
        assert game.getAllSims().size() == 2;
        
        game.createSim("Sim3", SimType.ADULT);
        assert game.getAllSims().size() == 3;
        game.shutdown();
        System.out.println("✓ testSimCount");
    }

    public static void main(String[] args) {
        testCreateAdultSim();
        testCreateChildSim();
        testGetActiveSim();
        testSetActiveSim();
        testMultipleSims();
        testSimStartsAtStreet();
        testSimStartsWithJobless();
        testSimIsAliveFresh();
        testSimHasNeeds();
        testSimCount();
    }
}
