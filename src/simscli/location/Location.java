package simscli.location;

import simscli.actions.Action;
import simscli.sims.Sim;

import java.util.List;

public abstract class Location {
    public abstract String key();   // "home", "park", ...
    public abstract String name();  // "Home", "Park", ...

    /** Which actions are available in this location. */
    public abstract List<Action> actions();

    /** Optional rule hook: can Sim enter? */
    public boolean canEnter(Sim sim) {
        return sim != null && sim.isAlive();
    }

    /** Optional message when entering. */
    public String onEnter(Sim sim) {
        return sim.getName() + " arrived at " + name() + ".";
    }
}