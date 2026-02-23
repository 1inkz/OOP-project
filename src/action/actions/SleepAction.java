package action.actions;

import action.Action;
import action.ActionContext;
import action.NeedDelta;
import action.Outcome;

public class SleepAction implements Action {
    public String name(){return "Sleep";}

    public boolean canPerform(ActionContext ctx){return true;}

    public Outcome perform(ActionContext ctx){
        return new Outcome().needs(new NeedDelta(0, +40, 0, 0))
                .msg(ctx.actor.getName()+"Slept Well.");
    }

}
