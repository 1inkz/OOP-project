package simscli.game;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages game time: clock advancement, hourly decays, time-based rules, and NPC behavior.
 * Encapsulates all time-related logic and scheduling.
 */
public class TimeManager {
    private GameClock clock;
    private final ExecutorService npcPool;
    private final GameLogger logger;

    public TimeManager(GameClock clock, GameLogger logger) {
        this.clock = clock;
        this.logger = logger;
        this.npcPool = Executors.newFixedThreadPool(
                Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()))
        );
    }

    public GameClock getClock() {
        return clock;
    }

    public void setClock(GameClock newClock) {
        this.clock = newClock;
    }

    public String getTimeString() {
        return clock.getFormattedTime();
    }

    /**
     * Advances game time by given minutes.
     * Applies decay to all sims, checks rules, and removes dead sims.
     */
    public void advanceGameTime(int minutes, List<Sim> sims, Game game) {
        clock.spendMinutes(minutes);
        int hours = minutes / 60;

        for (Sim sim : sims) {
            if (sim.isAlive()) {
                for (int i = 0; i < hours; i++) {
                    sim.applyEffect(sim.hourlyDecay());
                    sim.updatePetsHourly();
                }
            }
        }

        checkTimeRules(sims);
    }

    /**
     * Advances time for a single action execution.
     * Includes NPC auto-behavior via threading.
     */
    public void advanceTimeForAction(List<Sim> sims, int activeIndex, Game game) {
        advanceGameTime(60, sims, game);

        // NPC behaviour via thread pool
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < sims.size(); i++) {
            if (i == activeIndex) continue;

            Sim sim = sims.get(i);
            tasks.add(() -> {
                autoHelpIfCritical(sim, game);
                return null;
            });
        }

        try {
            npcPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Real-time advance: accumulates delta seconds and converts to game minutes.
     */
    public void autoAdvanceRealTime(double deltaSeconds, List<Sim> sims, Game game) {
        clock.advanceByRealTime(deltaSeconds);
        int hoursPassed = clock.getHoursPassedFromAccumulator();

        if (hoursPassed > 0) {
            advanceGameTime(hoursPassed * 60, sims, game);
        }
    }

    /**
     * Sims energy drops faster after 11pm
     */
    public void checkTimeRules(List<Sim> sims) {
        int currentHour = clock.getHour();

        if (currentHour >= 23 || currentHour <= 7) {
            logger.warn("\n[GAME] It's Midnight — Sim's energy draining fast!");
            
            for (Sim sim : sims) { {
                sim.applyEffect(Effect.none()
                        .plus(NeedType.ENERGY, -10));
            }}

        }
    }

    /**
     * Auto-help NPCs by performing critical need actions.
     */
    private void autoHelpIfCritical(Sim sim, Game game) {
        if (!sim.isAlive()) return;

        if (sim.isCritical(NeedType.HUNGER)) {
            ActionFactory.create(ActionType.EAT_SNACK).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.ENERGY)) {
            ActionFactory.create(ActionType.NAP).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.BLADDER)) {
            ActionFactory.create(ActionType.USE_TOILET).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.HYGIENE)) {
            if (sim.getOwnedHouse() != null) {
                ActionFactory.create(ActionType.BRUSH_TEETH).perform(sim, new GameContext(game));
            } else {
                ActionFactory.create(ActionType.CLEAN_PUBLIC).perform(sim, new GameContext(game));
            }
        }
    }

    /**
     * Cleanup: shutdown thread pool to prevent resource leaks.
     */
    public void shutdown() {
        npcPool.shutdownNow();
    }
}