package action.actions;

import action.*;

public class ShowerAction implements Action{
    public String name(){return "Shower";}

    public boolean canPerform(ActionContext ctx){return true;}

    public Outcome perform(ActionContext ctx){
        return new Outcome().needs(new NeedDelta(0,0,+35,0))
                .msg(ctx.actor.getName()+"took a shower");
    }
}
