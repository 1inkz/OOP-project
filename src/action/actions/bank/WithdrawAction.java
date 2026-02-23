package action.actions.bank;

import action.*;
import java.util.Scanner;

public class WithdrawAction implements Action {
    @Override
    public String name(){return "Withdraw money";}

    @Override
    public boolean canPerform(ActionContext ctx){
        return ctx.actor.getBankbalance().bankBalance > 0;
    }

    @Override
    public Outcome perform(ActionContext ctx) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter amount to withdraw: ");
        int amount = sc.nextInt();

        if (amount <= 0)
            return new Outcome().msg("Invalid amount.");

        if (amount > ctx.actor.getBankbalance().bankBalance)
            return new Outcome().msg("Not enough money in bank.");

        ctx.actor.getBankbalance().bankBalance -= amount;

        return new Outcome()
                .money(+amount)
                .msg("Withdrew $" + amount)
                .msg("Bank balance: $" + ctx.actor.getBankbalance().bankBalance);
    }
}
