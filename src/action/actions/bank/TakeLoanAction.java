package action.actions.bank;

import action.*;
import java.util.Scanner;

public class TakeLoanAction implements Action {
    private static final int MAX_LOAN = 1000;

    @Override
    public String name() {
        return "Take loan";
    }

    @Override
    public boolean canPerform(ActionContext ctx) {
        return ctx.actor.getBankbalance().loanBalance < MAX_LOAN;
    }

    @Override
    public Outcome perform(ActionContext ctx) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter loan amount (max $" + MAX_LOAN + "): ");
        int amount = sc.nextInt();

        if (amount <= 0)
            return new Outcome().msg("Invalid amount.");

        int currentLoan = ctx.actor.getBankbalance().loanBalance;
        if (currentLoan + amount > MAX_LOAN)
            return new Outcome().msg("Loan exceeds credit limit.");

        ctx.actor.getBankbalance().loanBalance += amount;

        return new Outcome()
                .money(+amount)
                .msg("Loan approved: +$" + amount)
                .msg("Total loan owed: $" + ctx.actor.getBankbalance().loanBalance);
    }
}
