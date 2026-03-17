package simscli.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import simscli.SaveGame;
import simscli.jobs.JobFactory;
import simscli.location.Location;
import simscli.sims.AdultSim;
import simscli.sims.ChildSim;
import simscli.sims.ElderSim;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

/**
 * Manages Sim lifecycle: creation, retrieval, removal, and death checking.
 * Encapsulates all Sim collection operations.
 */
public class SimManager {
    private final List<Sim> sims;
    private int activeIndex = -1;
    private final GameLogger logger;

    public SimManager(GameLogger logger) {
        this.sims = new ArrayList<>();
        this.logger = logger;
    }

    public List<Sim> getAllSims() {
        return Collections.unmodifiableList(sims);
    }

    public Sim getActiveSim() {
        if (activeIndex < 0 || activeIndex >= sims.size()) return null;
        return sims.get(activeIndex);
    }

    public void setActiveSim(int index) {
        if (index < 0 || index >= sims.size()) {
            throw new IllegalArgumentException("Bad index: " + index);
        }
        activeIndex = index;
        
        Sim activeSim = sims.get(activeIndex);
        List<String> pendingMessages = activeSim.getAndClearPendingLoanMessages();
        if (!pendingMessages.isEmpty()) {
            logger.warn("\n[Sims Reminder] You have switched to " + activeSim.getName() + ", below is unread message");
            for (String msg : pendingMessages) {
                logger.info(msg + "\n");
            }
        }
    }

    public int getActiveSimIndex() {
        return this.activeIndex;
    }

    public void addSim(Sim sim) {
        this.sims.add(sim);
    }

    public void clearSimList() {
        this.sims.clear();
    }

    public Sim createSim(String name, SimType type, Game game) {
        Sim sim;
        switch (type) {
            case CHILD: sim = new ChildSim(name, game); break;
            case ADULT: sim = new AdultSim(name, game); break;
            case ELDER: sim = new ElderSim(name, game); break;
            default: throw new IllegalArgumentException("unknown type");
        }

        sim.setLocation(game.location().get("street"));
        sim.setJob(JobFactory.create("jobless"));
        sim.setGame(game);
        sims.add(sim);
        if (activeIndex == -1) activeIndex = 0;
        return sim;
    }

    /**
     * Checks for dead sims and removes them.
     * Handles active sim switching if needed.
     */
    public void removeDeadSims(Game game) {
        String deadActiveSimName = null;
        String deadReason = null;

        Iterator<Sim> it = sims.iterator();
        int index = 0;

        while (it.hasNext()) {
            Sim sim = it.next();

            if (!sim.isAlive()) {
                String reason = getZeroNeedReason(sim);
                logger.error(sim.getName() + " was eliminated because " + reason + " reached 0!");

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
            activeIndex = -1;
            SaveGame.saveGame(game);
            return;
        }

        if (deadActiveSimName != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            activeIndex = 0;

            logger.error("\n[Eliminated] " + deadActiveSimName + " can no longer be played because "
                    + deadReason + " reached 0.");
            logger.info("Switching to " + sims.get(activeIndex).getName() + "...\n");

            SaveGame.saveGame(game);
        } else if (activeIndex >= sims.size()) {
            activeIndex = 0;
            SaveGame.saveGame(game);
        }
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
