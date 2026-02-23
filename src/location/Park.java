package location;

import action.Action;
import action.actions.ShowerAction;
import action.actions.SleepAction;
import action.actions.park.JogAction;
import sim.Sim;
import world.World;

import java.util.List;

public class Park extends Location {
    public Park(){super("Park");}

    @Override
    public List<Action> availableActions(Sim sim, World world){
        return List.of(new JogAction());
    }
}
