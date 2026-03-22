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

        logger.warn(sim.getName() + " collapsed from severe sickness and was moved to hospital.");

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
            sim.getNeeds().set(NeedType.HYGIENE, Math.max(sim.getNeeds().get(NeedType.HYGIENE), 45));
        }
    }

    private void handleSocialFunCrisis(Sim sim, List<Sim> sims, Game game, GameLogger logger) {
        boolean socialTriggered = sim.consumeZeroNeedTrigger(NeedType.SOCIAL);
        boolean funTriggered = sim.consumeZeroNeedTrigger(NeedType.FUN);
        if (!socialTriggered && !funTriggered) {
            return;
        }

        logger.warn(sim.getName() + " entered depression/burnout and is forced into deep sleep for 2 days.");

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

        // During forced sleep, world time still passes and lethal needs can kill the Sim.
        // The affected Sim uses a slower resting decay so this is risky but not guaranteed death.
        advanceBurnoutSleepHours(game, sims, sim, BURNOUT_SLEEP_HOURS);

        if (sim.isAlive()) {
            sim.getNeeds().set(NeedType.SOCIAL, 30);
            sim.getNeeds().set(NeedType.FUN, 30);
        }
    }

    private void handleBladderCrisis(Sim sim, GameLogger logger) {
        if (!sim.consumeZeroNeedTrigger(NeedType.BLADDER)) {
            return;
        }

        logger.warn(sim.getName() + " had an embarrassing accident.");
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
                            .plus(NeedType.BLADDER, -1)
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

        for (int i = 0; i < hours; i++) {
            for (Sim target : sims) {
                if (!target.isAlive()) {
                    continue;
                }

                if (target == patient) {
                    target.applyEffect(Effect.none()
                            .plus(NeedType.HUNGER, -1)
                            .plus(NeedType.ENERGY, -1)
                            .plus(NeedType.HYGIENE, -1)
                            .plus(NeedType.BLADDER, -1)
                            .plus(NeedType.SOCIAL, +1)
                            .plus(NeedType.FUN, +1));
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
