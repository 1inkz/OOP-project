package action;

import location.Location;
import sim.Sim;
import world.World;

public class ActionContext {
    public final Sim actor;
    public final World world;
    public final Location location;

    public ActionContext(Sim actor, World world, Location location){
        this.actor = actor;
        this.world = world;
        this.location = location;
    }
}
