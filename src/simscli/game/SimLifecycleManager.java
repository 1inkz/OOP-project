package simscli.game;

import java.util.Iterator;
import java.util.List;
import simscli.SaveGame;
import simscli.sims.Sim;
import simscli.stats.NeedType;

public final class SimLifecycleManager {

    public int removeDeadSims(List<Sim> sims, int activeIndex, Game game) {
        String deadActiveSimName = null;
        String deadReason = null;

        Iterator<Sim> it = sims.iterator();
        int index = 0;

        while (it.hasNext()) {
            Sim sim = it.next();

            if (!sim.isAlive()) {
                String reason = getZeroNeedReason(sim);
                System.out.println(sim.getName() + " was eliminated because " + reason + " reached 0!");

                if (index == activeIndex) {
                    deadActiveSimName = sim.getName();
                    deadReason = reason;
                }

                it.remove();
                continue;
            }

            index++;
        }

        if (sims.isEmpty()) {
            SaveGame.saveGame(game);
            return -1;
        }

        if (deadActiveSimName != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            activeIndex = 0;

            System.out.println("\n[Eliminated] " + deadActiveSimName
                    + " can no longer be played because " + deadReason + " reached 0.");
            System.out.println("Switching to " + sims.get(activeIndex).getName() + "...\n");

            SaveGame.saveGame(game);
        } else if (activeIndex >= sims.size()) {
            activeIndex = 0;
            SaveGame.saveGame(game);
        }

        return activeIndex;
    }

    private String getZeroNeedReason(Sim sim) {
        if (sim.getNeeds().get(NeedType.HUNGER) <= 0) return "HUNGER";
        if (sim.getNeeds().get(NeedType.ENERGY) <= 0) return "ENERGY";
        if (sim.getNeeds().get(NeedType.HYGIENE) <= 0) return "HYGIENE";
        if (sim.getNeeds().get(NeedType.SOCIAL) <= 0) return "SOCIAL";
        if (sim.getNeeds().get(NeedType.FUN) <= 0) return "FUN";
        if (sim.getNeeds().get(NeedType.BLADDER) <= 0) return "BLADDER";
        return "an unknown need";
    }
}