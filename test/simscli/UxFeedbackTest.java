package simscli;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.game.GameClock;
import simscli.game.GameLogger;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

/**
 * UX feedback tests for flavor text and end-of-day summary output.
 */
public class UxFeedbackTest {

    @Test
    public void performActionShouldIncludeFlavorLineForCommonActions() {
        Game game = new Game();
        game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);
        game.changeJob("Chef");

        String msg = game.performAction(ActionFactory.create(ActionType.WORK));

        assertTrue(msg.toLowerCase().contains("earned"));
        assertTrue(msg.contains("[Vibe]"));

        game.shutdown();
    }

    @Test
    public void dayRolloverShouldLogEndOfDaySummary() {
        CapturingLogger logger = new CapturingLogger();
        Game game = new Game(logger);
        Sim sim = game.createSim("Mia", SimType.ADULT);
        game.setActiveSim(0);

        game.changeJob("Chef");
        game.performAction(ActionFactory.create(ActionType.WORK));
        sim.getNeeds().set(NeedType.HUNGER, 35);

        game.setClock(new GameClock(1, 1380));
        game.advanceTimeForAction();

        boolean foundSummary = logger.infoMessages.stream()
                .anyMatch(m -> m.contains("[Day 1 Summary]")
                && m.contains("Money ")
                && m.contains("Skills ")
                && m.contains("Events ")
                && m.contains("Tip "));

        assertTrue(foundSummary);

        game.shutdown();
    }

    private static final class CapturingLogger implements GameLogger {
        private final List<String> infoMessages = new ArrayList<>();

        @Override
        public void info(String message) {
            infoMessages.add(message);
        }

        @Override
        public void warn(String message) {
            infoMessages.add(message);
        }

        @Override
        public void error(String message) {
            infoMessages.add(message);
        }
    }
}
