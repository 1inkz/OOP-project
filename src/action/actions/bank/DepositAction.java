package action.actions.bank;

import action.*;

import java.util.Scanner;

public class DepositAction implements Action{
    @Override public String name() { return "Deposit money";}

    @Override
    public boolean canPerform(ActionContext ctx) {
        return ctx.actor.getMoney() > 0;
    }

    @Override
    public Outcome perform(ActionContext ctx) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter amount to deposit: ");
        int amount = sc.nextInt();

        if (amount <= 0) {
            return new Outcome().msg("Invalid amount.");
        }

        if (amount > ctx.actor.getMoney()) {
            return new Outcome().msg("Not enough cash.");
        }

        ctx.actor.getBankbalance().bankBalance += amount;

        return new Outcome()
                .money(-amount)
                .msg("Deposited $" + amount)
                .msg("Bank balance: $" + ctx.actor.getBankbalance().bankBalance);
    }
}
