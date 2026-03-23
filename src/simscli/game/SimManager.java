package simscli.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import simscli.SaveGame;
import simscli.jobs.JobFactory;
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

    /**
     * Creates a SimManager with the specified logger.
     * 
     * @param logger the GameLogger instance for logging Sim lifecycle events
     */
    public SimManager(GameLogger logger) {
        this.sims = new ArrayList<>();
        this.logger = logger;
    }

    /**
     * Gets an unmodifiable list of all Sims currently in the game.
     * 
     * @return an immutable view of all Sims
     */
    public List<Sim> getAllSims() {
        return Collections.unmodifiableList(sims);
    }

    /**
     * Gets the currently active (player-controlled) Sim.
     * 
     * @return the active Sim, or null if none is active
     */
    public Sim getActiveSim() {
        if (activeIndex < 0 || activeIndex >= sims.size()) return null;
        return sims.get(activeIndex);
    }

    /**
     * Sets the active (player-controlled) Sim by index.
     * 
     * Displays any pending loan notifications for the newly active Sim.
     * 
     * @param index the index of the Sim to make active
     * @throws IllegalArgumentException if index is out of bounds
     */
    public void setActiveSim(int index) {
    	if (index == -1) {
    		activeIndex = -1;
    	}
    	else if (index < 0 || index >= sims.size()) {
            throw new IllegalArgumentException("Bad index: " + index);
        }
    	else {
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

//        List<Sim> deadSims = new ArrayList<>();
//        for (Sim sim : sims) {
//            if (sim.isAlive() && sim.shouldDieFromInactivity()) {
//                sim.setAlive(false);
//                deadSims.add(sim);
//                logger.error("\u001B[31m[Inactivity Death] \u001B[0m" + sim.getName() + "  was elimated because got neglected for " + sim.getInactiveDays() + " days!");
//            }
//        }
//        
//        sims.removeAll(deadSims);
    }

    /**
     * Gets the index of the currently active Sim.
     * 
     * @return the active Sim's index, or -1 if no Sim is active
     */
    public int getActiveSimIndex() {
        return this.activeIndex;
    }

    /**
     * Adds an existing Sim to the managed collection.
     * 
     * @param sim the Sim to add
     */
    public void addSim(Sim sim) {
        this.sims.add(sim);
    }

    /**
     * Removes all Sims from the manager (typically used for new game initialization).
     */
    public void clearSimList() {
        this.sims.clear();
    }

    /**
     * Creates a new Sim with the specified name and type.
     * 
     * Initializes the Sim at the Street location with a jobless job.
     * If this is the first Sim, sets it as active.
     * 
     * @param name the Sim's display name
     * @param type the Sim's life stage (CHILD, ADULT, or ELDER)
     * @param game the Game instance for location and job setup
     * @return the newly created Sim
     * @throws IllegalArgumentException if type is unknown
     */
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
     * Checks for dead sims and removes them from the simulation.
     * Handles active sim switching and auto-save if the active Sim dies.
     * @param game the Game instance for location access and save operations
     */
    public void removeDeadSims(Game game) {
        Iterator<Sim> it = sims.iterator();
        String reason = "";

        while (it.hasNext()) {
            Sim sim = it.next();

            if (!sim.isAlive()) {
            	if (sim.shouldDieFromInactivity()) {
            		reason = "[GAME] " + sim.getName() + " died from being neglected for " + (game.getClock().getDayNumber() - sim.getLastInactiveDays()) + " days!";
            	}
            	else if (sim.getLoanOverdueDays(game) >= 80 && sim.getBankingComponent().isInsolvent(sim.getLoanAmount())) {
            		reason = "[GAME] " + sim.getName() + " died from bankruptcy";
            		sim.clearPendingLoanMessages();
            	}
            	else {
            		reason = "[GAME] " + sim.getName() + " was eliminated because " + getZeroNeedReason(sim) + " reached 0!";
            	}
                logger.error(reason);
                it.remove();
                SaveGame.saveGame(game);
                continue;
            }
        }
    }

    /**
     * Determines which need caused a Sim's death by finding which need is at or below 0.
     * 
     * @param sim the dead or dying Sim
     * @return the name of the critical need (HUNGER, ENERGY, HYGIENE, SOCIAL, FUN, BLADDER)
     */
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
