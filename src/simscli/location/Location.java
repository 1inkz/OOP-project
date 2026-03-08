package simscli.location;

import simscli.actions.Action;
import simscli.sims.Sim;

import java.util.List;

public abstract class Location {
    public abstract String key();   // "home", "park", ...
    public abstract String name();  // "Home", "Park", ...

    public abstract List<Action> actions(Sim sim);

    public boolean canEnter(Sim sim) {
        return sim != null && sim.isAlive();
    }

    public String onEnter(Sim sim) {
        return sim.getName() + " arrived at " + name() + ".";
    }
}