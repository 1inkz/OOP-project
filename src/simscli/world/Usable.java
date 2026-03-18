package simscli.world;

import simscli.actions.Action;

/**
 * Interface for world objects that can be used (bed, fridge, etc).
 */
public interface Usable {
    /**
     * Gets the command key for this object.
     * @return lowercase key like "fridge"
     */
    String key();
    
    /**
     * Gets the display name for this object.
     * @return human-readable name
     */
    String name();
    
    /**
     * Gets the action this object triggers when used.
     * @return Action to perform
     */
    Action action();
}