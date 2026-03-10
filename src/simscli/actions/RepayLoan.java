package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.ui.Input;

public final class RepayLoan implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private final Input in = new Input();
    String output = "";
    
    @Override public String name() { return "Repay Loan"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
    	int maxRepay = Math.min(sim.getSimcoin(), sim.getLoanAmount());
    	
    	if (sim.getLoanAmount() <= 0) {
    		output = RED + "No loan to repay." + RESET;
    	}
    	else if (maxRepay <= 0) {
    		output = RED + "You do not have enough Simcoin to repay the loan." + RESET;
    	}
    	else {
        	int repayAmt = in.intRange("Repay amount: $", 1, maxRepay);

            if (sim.spendSimcoin(repayAmt)) {
            	sim.getBankingSystem().repayLoan(repayAmt);
            	output = GREEN + "Repaid $" + repayAmt
            			+ "! | Simcoin: $" + sim.getSimcoin() 
                        + " |  Remaining loan: $" + sim.getLoanAmount() + RESET;
            }
    	}

		return output;
    }
}