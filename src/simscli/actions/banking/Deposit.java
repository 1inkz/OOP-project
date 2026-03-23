package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.request.ActionRequest;
import simscli.actions.request.AmountActionRequest;
import simscli.sims.Sim;

/**
 * Banking action that allows a Sim to deposit Simcoin into their bank account.
 *
 * <p>Prompts the user for a deposit amount up to the Sim's available Simcoin.
 * On success, the specified amount is transferred from the Sim's on-hand
 * balance to their bank deposit.</p>
 */

/**
 * Banking action: Deposit Simcoin into bank account.
 */
public final class Deposit implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    @Override public String name() { return "Deposit Simcoin"; }

    /**
    * Executes the deposit workflow for the specified Sim.
    *
    * @param sim the Sim performing the deposit
    * @param ctx the active game context, used to collect user input
    * @return a status message describing the outcome
    */

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter

        int depositAmt = ui.intRange("Enter deposit amount or press '0' to cancel: $", 0, sim.getSimcoin());
        return perform(sim, ctx, new AmountActionRequest(depositAmt));
    }

    @Override
    public String perform(Sim sim, GameContext ctx, ActionRequest request) {
        if (!(request instanceof AmountActionRequest amountRequest)) {
            return perform(sim, ctx);
        }

        int depositAmt = amountRequest.amount();
        
        if (depositAmt == 0) {
            return GREEN + "Transaction Cancelled" + RESET;
        }


        if (sim.getBankingSystem().deposit(depositAmt)) {
            sim.spendSimcoin(depositAmt);
            return GREEN + "Deposited $" + depositAmt + " | Simcoin: $" + sim.getSimcoin() + " | Savings Account: $" + sim.getBankDeposit() + RESET;
        }

        return RED + "An error occurred during deposit." + RESET;
    }
}
