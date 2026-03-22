package simscli.policy;

import java.util.List;
import simscli.game.Game;
import simscli.game.GameClock;
import simscli.game.GameLogger;
import simscli.location.Location;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Handles non-lethal need crises and forced events.
 * Lethal checks remain in SimStatsComponent for Hunger and Energy only.
 */
public final class NeedCrisisPolicy implements TimeRulePolicy {
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private static final int HOSPITAL_FEE = 50;
    private static final int HOSPITAL_REST_HOURS = 8;
    private static final int BURNOUT_SLEEP_HOURS = 48;

    @Override
    public void apply(GameClock clock, List<Sim> sims, GameLogger logger, Game game) {
        for (Sim sim : sims) {
            if (!sim.isAlive()) {
                continue;
            }

            handleHygieneCrisis(sim, sims, game, logger);
            if (!sim.isAlive()) {
                continue;
            }

            handleSocialFunCrisis(sim, sims, game, logger);
            if (!sim.isAlive()) {
                continue;
            }

            handleBladderCrisis(sim, logger);
        }
    }

    private void handleHygieneCrisis(Sim sim, List<Sim> sims, Game game, GameLogger logger) {
        if (!sim.consumeZeroNeedTrigger(NeedType.HYGIENE)) {
            return;
        }

        logger.warn(GREEN + sim.getName()
            + " was overwhelmed by illness and rushed to hospital for urgent care." + RESET);

        // Hospitalization fee. If insufficient funds, all remaining cash is consumed.
        if (!sim.spendSimcoin(HOSPITAL_FEE)) {
            sim.setSimcoin(0);
        }

        Location hospital = game.location().get("hospital");
        if (hospital != null) {
            sim.setLocation(hospital);
        }

        // Faint/rest period with hospital care (less punishing, still costs time).
        advanceHospitalizationHours(game, sims, sim, HOSPITAL_REST_HOURS);

        if (sim.isAlive()) {
            sim.getNeeds().set(NeedType.ENERGY, Math.max(sim.getNeeds().get(NeedType.ENERGY), 65));
            sim.getNeeds().set(NeedType.HYGIENE, Math.max(sim.getNeeds().get(NeedType.HYGIENE), 85));
            sim.getNeeds().set(NeedType.BLADDER, Math.max(sim.getNeeds().get(NeedType.BLADDER), 80));
        }
    }

    private void handleSocialFunCrisis(Sim sim, List<Sim> sims, Game game, GameLogger logger) {
        boolean socialTriggered = sim.consumeZeroNeedTrigger(NeedType.SOCIAL);
        boolean funTriggered = sim.consumeZeroNeedTrigger(NeedType.FUN);
        if (!socialTriggered && !funTriggered) {
            return;
        }

        logger.warn(GREEN + sim.getName()
            + " hit a burnout wall and drifts into a 2-day recovery sleep." + RESET);

        // Forced deep sleep location: Home if available, otherwise Park.
        if (sim.getOwnedHouse() != null) {
            Location home = game.location().get("home");
            if (home != null) {
                sim.setLocation(home);
            }
        } else {
            Location park = game.location().get("park");
            if (park != null) {
                sim.setLocation(park);
            }
        }

        // Forced recovery sleep should stabilize survival needs while time advances.
        // This mirrors deep-sleep intent: hunger can drop, but not to immediate starvation.
        advanceBurnoutSleepHours(game, sims, sim, BURNOUT_SLEEP_HOURS);

        if (sim.isAlive()) {
            sim.getNeeds().set(NeedType.SOCIAL, 35);
            sim.getNeeds().set(NeedType.FUN, 35);
        }
    }

    private void handleBladderCrisis(Sim sim, GameLogger logger) {
        if (!sim.consumeZeroNeedTrigger(NeedType.BLADDER)) {
            return;
        }

        logger.warn(GREEN + sim.getName() + " had an awkward little mishap. Deep breaths, reset, move on." + RESET);
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, -45)
                .plus(NeedType.SOCIAL, -45));

        // Resolve the immediate accident loop so event is not spammed every tick.
        sim.getNeeds().set(NeedType.BLADDER, 20);
    }

    private void advanceHospitalizationHours(Game game, List<Sim> sims, Sim patient, int hours) {
        game.getClock().spendMinutes(hours * 60);

        for (int i = 0; i < hours; i++) {
            for (Sim target : sims) {
                if (!target.isAlive()) {
                    continue;
                }

                if (target == patient) {
                    target.applyEffect(Effect.none()
                            .plus(NeedType.ENERGY, +4)
                            .plus(NeedType.HUNGER, +2)
                            .plus(NeedType.HYGIENE, +6)
                            .plus(NeedType.BLADDER, +8)
                            .plus(NeedType.SOCIAL, -1)
                            .plus(NeedType.FUN, -1));
                } else {
                    target.applyEffect(target.hourlyDecay());
                }

                target.updatePetsHourly();
            }

            if (!patient.isAlive()) {
                break;
            }
        }
    }

    private void advanceBurnoutSleepHours(Game game, List<Sim> sims, Sim patient, int hours) {
        game.getClock().spendMinutes(hours * 60);

        // Safety floor first: avoid triggering lethal checks before recovery loop starts.
        patient.getNeeds().set(NeedType.HUNGER, Math.max(patient.getNeeds().get(NeedType.HUNGER), 30));
        patient.getNeeds().set(NeedType.ENERGY, Math.max(patient.getNeeds().get(NeedType.ENERGY), 60));

        for (int i = 0; i < hours; i++) {
            for (Sim target : sims) {
                if (!target.isAlive()) {
                    continue;
                }

                if (target == patient) {
                    target.applyEffect(Effect.none()
                            .plus(NeedType.HUNGER, -1)
                            .plus(NeedType.ENERGY, +4)
                            .plus(NeedType.HYGIENE, -1)
                            .plus(NeedType.BLADDER, -1)
                            .plus(NeedType.SOCIAL, 0)
                            .plus(NeedType.FUN, 0));

                    // Keep burnout recovery aligned with deep sleep behavior: no starvation spiral.
                    target.getNeeds().set(NeedType.HUNGER, Math.max(target.getNeeds().get(NeedType.HUNGER), 30));
                    target.getNeeds().set(NeedType.ENERGY, Math.max(target.getNeeds().get(NeedType.ENERGY), 85));
                } else {
                    target.applyEffect(target.hourlyDecay());
                }

                target.updatePetsHourly();
            }

            if (!patient.isAlive()) {
                break;
            }
        }
    }
}
