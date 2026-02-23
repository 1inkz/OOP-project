package action.actions.bank;

import action.Action;
import action.ActionContext;
import action.Outcome;
import java.util.Scanner;

public class GetBalanceAction implements Action {
    @Override public String  name() {return "Get balance";}

    @Override
    public boolean canPerform(ActionContext ctx){
        return true;
    }

    @Override
    public Outcome perform(ActionContext ctx){
        return new Outcome()
                .msg("Cash: $" + ctx.actor.getMoney())
                .msg("Bank balance: $" + ctx.actor.getBankbalance().bankBalance)
                .msg("Loan owed: $" + ctx.actor.getBankbalance().loanBalance);
    }
}
