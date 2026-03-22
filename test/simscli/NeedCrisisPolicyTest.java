package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

/**
 * Verifies non-lethal crisis consequences and lethal hunger/energy behavior.
 */
public class NeedCrisisPolicyTest {

    @Test
    public void hygieneZeroCausesHospitalFaintAndFee() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.HYGIENE, 0);
        int beforeMoney = sim.getSimcoin();

        game.checkTimeRules();

        assertEquals("hospital", sim.getLocation().key());
        assertEquals(beforeMoney - 50, sim.getSimcoin());
        assertTrue(sim.getNeeds().get(NeedType.ENERGY) > 0);

        game.shutdown();
    }

    @Test
    public void bladderZeroTriggersEmbarrassingPenaltyWithoutDeath() {
        Game game = new Game();
        Sim sim = game.createSim("Kai", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.BLADDER, 0);
        int hygieneBefore = sim.getNeeds().get(NeedType.HYGIENE);
        int socialBefore = sim.getNeeds().get(NeedType.SOCIAL);

        game.checkTimeRules();

        assertTrue(sim.isAlive());
        assertTrue(sim.getNeeds().get(NeedType.HYGIENE) < hygieneBefore);
        assertTrue(sim.getNeeds().get(NeedType.SOCIAL) < socialBefore);

        game.shutdown();
    }

    @Test
    public void socialAndFunZeroCanKillDuringForcedTwoDaySleep() {
        Game game = new Game();
        Sim sim = game.createSim("Noah", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.HUNGER, 1);
        sim.getNeeds().set(NeedType.ENERGY, 1);
        sim.getNeeds().set(NeedType.SOCIAL, 0);
        sim.getNeeds().set(NeedType.FUN, 0);

        game.checkTimeRules();

        assertTrue(!sim.isAlive());

        game.shutdown();
    }

    @Test
    public void socialAndFunZeroWithModerateNeedsShouldSurviveForcedSleep() {
        Game game = new Game();
        Sim sim = game.createSim("Lia", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.HUNGER, 80);
        sim.getNeeds().set(NeedType.ENERGY, 80);
        sim.getNeeds().set(NeedType.SOCIAL, 0);
        sim.getNeeds().set(NeedType.FUN, 0);

        game.checkTimeRules();

        assertTrue(sim.isAlive());
        assertTrue(sim.getNeeds().get(NeedType.SOCIAL) >= 30);
        assertTrue(sim.getNeeds().get(NeedType.FUN) >= 30);

        game.shutdown();
    }

    @Test
    public void hungerZeroStillKillsImmediately() {
        Game game = new Game();
        Sim sim = game.createSim("Mia", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.HUNGER, 0);
        // Trigger death check via a no-op effect application path using tiny negative to another need.
        sim.getNeeds().add(NeedType.FUN, -1);
        sim.applyEffect(simscli.stats.Effect.none());

        assertTrue(!sim.isAlive());

        game.shutdown();
    }
}
