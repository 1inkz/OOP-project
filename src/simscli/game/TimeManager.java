package simscli.game;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.policy.MidnightEnergyDrainPolicy;
import simscli.policy.NeedCrisisPolicy;
import simscli.policy.TimeRulePolicy;
import simscli.sims.Sim;
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
    private final List<TimeRulePolicy> timeRulePolicies;
    private final DailySummaryService dailySummaryService;

    public TimeManager(GameClock clock, GameLogger logger) {
        this.clock = clock;
        this.logger = logger;
        this.timeRulePolicies = new ArrayList<>();
        this.timeRulePolicies.add(new NeedCrisisPolicy());
        this.timeRulePolicies.add(new MidnightEnergyDrainPolicy());
        this.dailySummaryService = new DailySummaryService();
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
        int dayBefore = clock.getDayNumber();
        dailySummaryService.captureDayStart(dayBefore, sims);
        clock.spendMinutes(minutes);
        int dayAfter = clock.getDayNumber();
        int hours = minutes / 60;

        for (Sim sim : sims) {
            if (sim.isAlive()) {
                for (int i = 0; i < hours; i++) {
                    sim.applyEffect(sim.hourlyDecay());
                    sim.applyHouseComfortBonus();
                    sim.updatePetsHourly();
                }
            }
        }

        if (dayAfter > dayBefore) {
            for (int day = dayBefore + 1; day <= dayAfter; day++) {
                dailySummaryService.logEndOfDay(day - 1, sims, logger);
                processDailyEconomy(sims, day);
                dailySummaryService.captureDayStart(day, sims);
            }
        }

        checkTimeRules(sims, game);
    }

    /**
     * Print status of day summary
     * 
     */
    private void processDailyEconomy(List<Sim> sims, int dayNumber) {
        for (Sim sim : sims) {
            if (!sim.isAlive()) {
                continue;
            }
            String summary = sim.processDailyAssetEconomy(dayNumber);
            if (!summary.isEmpty()) {
                logger.info("[Day " + dayNumber + "] " + sim.getName() + ": " + summary);
            }
        }
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

        clock.setAccumulatedMinutes(5); 

        int hoursPassed = clock.getHoursPassedFromAccumulator();

        if (hoursPassed > 0) {
            for (Sim sim : sims) {
                if (sim.isAlive()) {
                    sim.applyEffect(sim.hourlyDecay());
                }
            }
            game.getSimManager().removeDeadSims(game);
        }
    }
    
    /**
     * Sims energy drops faster after 11pm
     */
    public void checkTimeRules(List<Sim> sims, Game game) {
        for (TimeRulePolicy policy : timeRulePolicies) {
            policy.apply(clock, sims, logger, game);
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