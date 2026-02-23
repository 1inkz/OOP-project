package action.actions.bank;

import action.*;
import java.util.Scanner;

public class RepayLoanAction implements Action {
    @Override
    public String name() {
        return "Repay loan";
    }

    @Override
    public boolean canPerform(ActionContext ctx) {
        return ctx.actor.getBankbalance().loanBalance > 0 && ctx.actor.getMoney() > 0;
    }

    @Override
    public Outcome perform(ActionContext ctx) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter repayment amount: ");
        int amount = sc.nextInt();

        if (amount <= 0)
            return new Outcome().msg("Invalid amount.");

        int loan = ctx.actor.getBankbalance().loanBalance;
        int cash = ctx.actor.getMoney();

        int pay = Math.min(amount, Math.min(loan, cash));

        ctx.actor.getBankbalance().loanBalance -= pay;

        return new Outcome()
                .money(-pay)
                .msg("Repaid $" + pay)
                .msg("Remaining loan: $" + ctx.actor.getBankbalance().loanBalance);
    }

}
