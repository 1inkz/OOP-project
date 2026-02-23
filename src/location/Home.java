package location;

import action.Action;
import action.actions.ShowerAction;
import action.actions.SleepAction;
import sim.Sim;
import world.World;

import java.util.List;

public class Home extends Location{
    public Home(){super("Home");}

    @Override
    public List<Action> availableActions(Sim sim, World world){
        return List.of(new SleepAction(), new ShowerAction());
    }

}
