package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.sims.Sim;

public final class ApplyLoan implements Action {
    
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    String output = "";
    
    @Override public String name() { return "Apply Loan"; }

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
