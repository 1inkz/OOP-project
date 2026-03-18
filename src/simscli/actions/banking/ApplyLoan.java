package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
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
    String output = "";
    
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
        
        int loanAmt = ui.intRange(
                "Enter loan amount (Max: $" + simscli.bank.BankingSystem.getLoanLimit() + ") or press '0' to cancel: $",
                0,
                simscli.bank.BankingSystem.getLoanLimit()
                        - sim.getLoanAmount());

        if (loanAmt == 0) {
        	output = GREEN + "Transaction Cancelled";
        }
        else if (sim.getBankingSystem().applyLoan(loanAmt)) {
        	sim.earnSimcoin(loanAmt);
        	output = GREEN + "Loan approved! $" + loanAmt + " | Simcoin: $" + sim.getSimcoin() + " | Loan: $" + sim.getLoanAmount() + RESET;
        }
        
		return output;
    }
}
