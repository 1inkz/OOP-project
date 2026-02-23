package action.actions.park;

import action.Action;
import action.ActionContext;
import action.NeedDelta;
import action.Outcome;

public class JogAction implements Action {
    public String name(){return "Jog";}

    public boolean canPerform(ActionContext ctx){return true;}

    public Outcome perform(ActionContext ctx){
        return new Outcome().needs(new NeedDelta(0,-35,0,0))
                .msg(ctx.actor.getName()+"Run in the park");
    }
}
