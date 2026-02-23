package trait;

import sim.Sim;
import action.Action;
import action.Outcome;
import action.actions.SleepAction;

public class LazyTrait implements Trait {

    @Override
    public String name(){return "Lazy";};

    @Override
    public void modifyOutcome(Action action, Sim sim, Outcome outcome){

        if (action instanceof SleepAction){
            outcome.needDelta.energyDelta += 10;
            outcome.messages.add("Lazy trait: extra rest gained");
        }
    }
}
