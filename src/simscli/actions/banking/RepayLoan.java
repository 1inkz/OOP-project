package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.sims.Sim;

/**
 * Banking action that allows a Sim to repay part or all of their loan.
 *
 * <p>Prompts the user for a repayment amount up to the Sim's available Simcoin
 * and outstanding loan. On success, the specified amount is deducted from the
 * Sim's balance and applied to reduce the remaining loan.</p>
 */

/**
 * Banking action: Repay part or all of the outstanding loan.
 */
public final class RepayLoan implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    String output = "";

    @Override public String name() { return "Repay Loan"; }

    /**
     * Executes the loan repayment workflow for the specified Sim.
     *
     * @param sim the Sim performing the repayment
     * @param ctx the active game context, used to collect user input
     * @return a status message describing the outcome
     */
    
    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter
        
    	int maxRepay = Math.min(sim.getSimcoin(), sim.getLoanAmount());
    	
    	if (sim.getLoanAmount() <= 0) {
    		output = RED + "No loan to repay." + RESET;
    	}
    	else if (maxRepay <= 0) {
    		output = RED + "You do not have enough Simcoin to repay the loan." + RESET;
    	}
    	else {
        	int repayAmt = ui.intRange("Enter repay amount or press '0' to cancel: $", 0, maxRepay);

            if (repayAmt == 0) {
            	output = GREEN + "Transaction Cancelled" + RESET;
            }
            else if (sim.spendSimcoin(repayAmt)) {
            	sim.getBankingSystem().repayLoan(repayAmt);
                if (sim.getLoanAmount() == 0) {
                    sim.setLoanStartDay(0);
                }
            	output = GREEN + "Repaid $" + repayAmt
            			+ "! | Simcoin: $" + sim.getSimcoin() 
                        + " |  Remaining loan: $" + sim.getLoanAmount() + RESET;
            }
            else {
            	output = RED + "You do not have enough Simcoin to repay the loan." + RESET;
            }
    	}

		return output;
    }
}
