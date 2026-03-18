package simscli;

import simscli.game.Game;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

/**
 * Test: Integration tests for game simulation and gameplay mechanics.
 */
public class SimulationIntegrationTest {
    
    public static void testSimCanWork() {
        Game game = new Game();
        Sim sim = game.createSim("Worker", SimType.ADULT);
        game.setActiveSim(0);
        
        int moneyBefore = sim.getSimcoin();
        game.performAction(ActionFactory.create(ActionType.WORK));
        int moneyAfter = sim.getSimcoin();
        
        assert moneyAfter > moneyBefore : "Working should earn money";
        game.shutdown();
        System.out.println("✓ testSimCanWork");
    }

    public static void testLocationTravel() {
        Game game = new Game();
        Sim sim = game.createSim("Traveler", SimType.ADULT);
        game.setActiveSim(0);
        
        String before = sim.getLocation().key();
        game.travelTo("park");
        String after = sim.getLocation().key();
        
        assert !before.equals(after) : "Location should change";
        assert after.equals("park") : "Should be at park";
        game.shutdown();
        System.out.println("✓ testLocationTravel");
    }

    public static void testBankingIntegration() {
        Game game = new Game();
        Sim sim = game.createSim("Banker", SimType.ADULT);
        game.setActiveSim(0);
        
        int initialMoney = sim.getSimcoin();
        sim.applyLoan(500);
        int afterLoan = sim.getSimcoin();
        
        assert afterLoan == initialMoney + 500 : "Loan should increase money";
        
        sim.repayLoan(300);
        assert sim.getSimcoin() == afterLoan : "Repay reduces loan, not deposit";
        
        game.shutdown();
        System.out.println("✓ testBankingIntegration");
    }

    public static void testNeedsDecay() {
        Game game = new Game();
        Sim sim = game.createSim("Worker", SimType.ADULT);
        game.setActiveSim(0);
        
        int hungerBefore = sim.getNeeds().get(NeedType.HUNGER);
        
        // Work should consume hunger
        game.performAction(ActionFactory.create(ActionType.WORK));
        
        int hungerAfter = sim.getNeeds().get(NeedType.HUNGER);
        assert hungerAfter < hungerBefore : "Work should decrease hunger";
        
        game.shutdown();
        System.out.println("✓ testNeedsDecay");
    }

    public static void testMultipleActions() {
        Game game = new Game();
        Sim sim = game.createSim("Busy", SimType.ADULT);
        game.setActiveSim(0);
        
        int moneyBefore = sim.getSimcoin();
        
        for (int i = 0; i < 3; i++) {
            game.performAction(ActionFactory.create(ActionType.WORK));
        }
        
        int moneyAfter = sim.getSimcoin();
        assert moneyAfter > moneyBefore : "Multiple works should earn more";
        
        game.shutdown();
        System.out.println("✓ testMultipleActions");
    }

    public static void testSimInitialState() {
        Game game = new Game();
        Sim sim = game.createSim("Fresh", SimType.ADULT);
        
        assert sim != null : "Sim created";
        assert sim.getLocation() != null : "Has location";
        assert sim.getJob() != null : "Has job";
        assert sim.getNeeds() != null : "Has needs";
        assert sim.isAlive() : "Is alive";
        
        game.shutdown();
        System.out.println("✓ testSimInitialState");
    }

    public static void testGameTime() {
        Game game = new Game();
        
        assert game.time() != null : "Game has time manager";
        assert game.time().getClock() != null : "Has game clock";
        
        game.shutdown();
        System.out.println("✓ testGameTime");
    }

    public static void testAllLocationsAccessible() {
        Game game = new Game();
        Sim sim = game.createSim("Explorer", SimType.ADULT);
        game.setActiveSim(0);
        
        String[] locations = {"street", "park", "bank", "restaurant", "hospital", "petstore"};
        
        for (String loc : locations) {
            String result = game.travelTo(loc);
            assert result != null : "Travel should return message for " + loc;
        }
        
        game.shutdown();
        System.out.println("✓ testAllLocationsAccessible");
    }

    public static void testMultiSimGameplay() {
        Game game = new Game();
        Sim sim1 = game.createSim("Sim1", SimType.ADULT);
        Sim sim2 = game.createSim("Sim2", SimType.CHILD);
        Sim sim3 = game.createSim("Sim3", SimType.ELDER);
        
        assert game.getAllSims().size() == 3;
        
        game.setActiveSim(0);
        assert game.activeSim().getName().equals("Sim1");
        
        game.setActiveSim(2);
        assert game.activeSim().getName().equals("Sim3");
        
        game.shutdown();
        System.out.println("✓ testMultiSimGameplay");
    }

    public static void testSimSurvivesSelfCare() {
        Game game = new Game();
        Sim sim = game.createSim("Survivor", SimType.ADULT);
        game.setActiveSim(0);
        
        assert sim.isAlive() : "Starts alive";
        
        // Perform some actions to maintain needs
        for (int i = 0; i < 5; i++) {
            if (sim.getNeeds().isCritical(NeedType.HUNGER)) {
                game.performAction(ActionFactory.create(ActionType.EAT_SNACK));
            } else {
                game.performAction(ActionFactory.create(ActionType.WORK));
            }
        }
        
        assert sim.isAlive() : "Should survive with self-care";
        game.shutdown();
        System.out.println("✓ testSimSurvivesSelfCare");
    }

    public static void main(String[] args) {
        testSimCanWork();
        testLocationTravel();
        testBankingIntegration();
        testNeedsDecay();
        testMultipleActions();
        testSimInitialState();
        testGameTime();
        testAllLocationsAccessible();
        testMultiSimGameplay();
        testSimSurvivesSelfCare();
    }
}
