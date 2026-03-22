package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.request.ActionRequest;
import simscli.actions.request.AmountActionRequest;
import simscli.sims.Sim;

/**
 * Banking action that allows a Sim to withdraw Simcoin from their bank account.
 *
 * <p>Prompts the user for a withdrawal amount up to the Sim's available bank
 * deposit. On success, the specified amount is transferred from the bank
 * balance to the Sim's on-hand Simcoin.</p>
 */

/**
 * Banking action: Withdraw Simcoin from bank account.
 */
public final class Withdraw implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    @Override public String name() { return "Withdraw Simcoin"; }

    /**
     * Executes the withdrawal workflow for the specified Sim.
     *
     * @param sim the Sim performing the withdrawal
     * @param ctx the active game context, used to collect user input
     * @return a status message describing the outcome
     */

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter

        if (sim.getBankDeposit() <= 0) {
            return RED + "No money available to withdraw!" + RESET;
        }

        int withdrawAmt = ui.intRange("Enter withdraw amount or press '0' to cancel: $", 0, sim.getBankDeposit());
        return perform(sim, ctx, new AmountActionRequest(withdrawAmt));
    }

    @Override
    public String perform(Sim sim, GameContext ctx, ActionRequest request) {
        if (!(request instanceof AmountActionRequest amountRequest)) {
            return perform(sim, ctx);
        }

        int withdrawAmt = amountRequest.amount();
        if (withdrawAmt < 0 || withdrawAmt > sim.getBankDeposit()) {
            return RED + "Invalid withdrawal amount." + RESET;
        }

        if (withdrawAmt == 0) {
            return GREEN + "Transaction Cancelled" + RESET;
        }

        if (sim.getBankingSystem().withdraw(withdrawAmt)) {
            sim.earnSimcoin(withdrawAmt);
            return GREEN + "Withdrew $" + withdrawAmt + " | Simcoin: $" + sim.getSimcoin() + " | Savings Account: $" + sim.getBankDeposit() + RESET;
        }

        return RED + "An error occurred during withdrawal." + RESET;
    }
}
