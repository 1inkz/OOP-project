package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.request.ActionRequest;
import simscli.actions.request.AmountActionRequest;
import simscli.sims.Sim;

/**
 * Banking action that allows a Sim to apply for a new loan.
 *
 * <p>Prompts the user for an amount within the Sim's remaining loan capacity,
 * then delegates approval to {@link simscli.bank.BankingSystem}. On success,
 * the borrowed amount is credited to the Sim's Simcoin balance.</p>
 */

/**
 * Banking action: Apply for a loan up to the system limit.
 */
public final class ApplyLoan implements Action {
    
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    @Override public String name() { return "Apply Loan"; }

/**
 * Executes the loan application workflow for the specified Sim.
 *
 * @param sim the Sim requesting the loan
 * @param ctx the active game context, used to collect the loan amount from the user
 * @return a status message describing the outcome
 */

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter

        int maxLoan = simscli.bank.BankingSystem.getLoanLimit() - sim.getLoanAmount();
        int loanAmt = ui.intRange(
            "Enter loan amount (Max: $" + maxLoan + ") or press '0' to cancel: $",
                0,
                maxLoan);

        return perform(sim, ctx, new AmountActionRequest(loanAmt));
    }

    @Override
    public String perform(Sim sim, GameContext ctx, ActionRequest request) {
        if (!(request instanceof AmountActionRequest amountRequest)) {
            return perform(sim, ctx);
        }

        int loanAmt = amountRequest.amount();

        if (loanAmt == 0) {
            return GREEN + "Transaction Cancelled" + RESET;
        }

        if (sim.getBankingSystem().applyLoan(loanAmt)) {
            sim.earnSimcoin(loanAmt);
            return GREEN + "Loan approved! $" + loanAmt + " | Simcoin: $" + sim.getSimcoin() + " | Loan: $" + sim.getLoanAmount() + RESET;
        }

        return "Loan application failed.";
    }
}
