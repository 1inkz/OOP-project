package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.sims.Sim;

public final class RepayLoan implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    String output = "";
    
    @Override public String name() { return "Repay Loan"; }

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
            	output = GREEN + "Transaction Cancelled";
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
    	}

		return output;
    }
}
