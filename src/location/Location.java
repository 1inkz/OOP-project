package location;

import action.Action;
import sim.Sim;
import world.World;

import java.util.*;

public abstract class Location {
    protected final String location_name;

    public Location(String name){
        this.location_name = name;
    }

    public String getLocation(){
        return location_name;
    }

    public abstract List<Action> availableActions(Sim sim, World world);
}
