package simscli.location;

import simscli.actions.Action;
import simscli.sims.Sim;

import java.util.List;

/**
 * Abstract base class for all game locations.
 * Locations provide actions and manage Sim entry conditions.
 */
public abstract class Location {
    /**
     * Gets the unique key identifier for this location.
     * @return lowercase location key (e.g., "home", "park")
     */
    public abstract String key();   // "home", "park", ...
    /**
     * Gets the display name of this location.
     * @return human-readable location name
     */
    public abstract String name();  // "Home", "Park", ...

    /**
     * Gets available actions for a Sim at this location.
     * @param sim the Sim requesting actions
     * @return list of available Action objects
     */
    public abstract List<Action> actions(Sim sim);

    /**
     * Determines if a Sim can enter this location.
     * @param sim the Sim checking entry
     * @return true if entry is allowed, false otherwise
     */
    public boolean canEnter(Sim sim) {
        return sim != null && sim.isAlive();
    }

    /**
     * Returns the message displayed when a Sim enters this location.
     * @param sim the Sim entering
     * @return entry message
     */
    public String onEnter(Sim sim) {
        return sim.getName() + " arrived at " + name() + ".";
    }
}