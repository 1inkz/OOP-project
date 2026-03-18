package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
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
    String output = "";
    
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
        
        if (sim.getSimcoin() <= 0) {
        	output = RED + "You have no Simcoin to deposit!" + RESET;
        }
        else {
            int depositAmt = ui.intRange("Enter deposit amount or press '0' to cancel: $", 0, sim.getSimcoin());

            if (depositAmt == 0) {
            	output = GREEN + "Transaction Cancelled";
            }
            else if (sim.getBankingSystem().deposit(depositAmt)) {
            	sim.spendSimcoin(depositAmt);
            	output = GREEN + "Deposited $" + depositAmt + " | Simcoin: $" + sim.getSimcoin() +" | Deposit: $" + sim.getBankDeposit() + RESET;
            }
        }
		return output;
    }
}
