package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.game.GameClock;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Verifies midnight energy drain applies only at night and ignores dead Sims.
 */
public class MidnightEnergyDrainPolicyTest {

    @Test
    public void shouldNotDrainEnergyDuringDaytime() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.ENERGY, 70);
        game.setClock(new GameClock(1, 12 * 60)); // 12:00

        game.checkTimeRules();

        assertEquals(70, sim.getNeeds().get(NeedType.ENERGY));
        game.shutdown();
    }

    @Test
    public void shouldDrainEnergyAtNight() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.ENERGY, 70);
        game.setClock(new GameClock(1, 23 * 60)); // 23:00

        game.checkTimeRules();

        assertEquals(60, sim.getNeeds().get(NeedType.ENERGY));
        game.shutdown();
    }

    @Test
    public void shouldNotAffectDeadSims() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        sim.getNeeds().set(NeedType.HUNGER, 0);
        sim.applyEffect(Effect.none()); // triggers death condition

        int energyBefore = sim.getNeeds().get(NeedType.ENERGY);
        game.setClock(new GameClock(1, 23 * 60));

        game.checkTimeRules();

        assertEquals(energyBefore, sim.getNeeds().get(NeedType.ENERGY));
        game.shutdown();
    }
}
