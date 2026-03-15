package simscli.game;

import java.util.List;
import simscli.sims.Sim;
import simscli.stats.NeedType;

public final class GameTimeManager {

    public void advanceGameTime(Game game, List<Sim> sims, int minutes) {
        game.getClock().spendMinutes(minutes);

        int hours = minutes / 60;

        for (Sim sim : sims) {
            if (!sim.isAlive()) continue;

            for (int i = 0; i < hours; i++) {
                sim.applyEffect(sim.hourlyDecay());
            }
        }

        applyDailyRules(game, sims, false);
    }

    public void applyDailyRules(Game game, List<Sim> sims, boolean forceNextMorning) {
        int currentHour = game.getClock().getHour();

        if (currentHour == 20 && !forceNextMorning) {
            System.out.println("\n[GAME] It's 8pm — all sims should head to bed!");
        } else if (currentHour == 21 && !forceNextMorning) {
            System.out.println("\n[GAME] It's 9pm — sleep now!");
        }

        if (currentHour == 22 || forceNextMorning) {
            game.getClock().resetToNextDayMorning();

            for (Sim sim : sims) {
                if (!sim.isAlive()) continue;

                boolean hasHouse = sim.getOwnedHouse() != null;
                boolean inCorrectLocation =
                        (hasHouse && sim.getLocation().key().equals("home")) ||
                        (!hasHouse && sim.getLocation().key().equals("park"));

                if (!inCorrectLocation && sim == game.activeSim()) {
                    System.out.println("\nYou are too tired! Forced to sleep... See you next morning 8:00 AM!");
                }

                sim.getBankingSystem().settleInterest();
                sim.getNeeds().set(NeedType.ENERGY, 90);
                sim.getNeeds().set(NeedType.HUNGER, 30);
            }
        }
    }
}