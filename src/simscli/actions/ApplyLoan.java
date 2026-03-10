package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.ui.Input;

public final class ApplyLoan implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private final Input in = new Input();
    String output = "";
    
    @Override public String name() { return "Apply Loan"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        int loanAmt = in.intRange(
                "Loan amount (max $" + simscli.bank.BankingSystem.getLoanLimit() + "): $",
                1,
                simscli.bank.BankingSystem.getLoanLimit()
                        - sim.getLoanAmount());

        if (sim.getBankingSystem().applyLoan(loanAmt)) {
        	sim.earnSimcoin(loanAmt);
        	output = GREEN + "Loan approved! $" + loanAmt + " | Simcoin: $" + sim.getSimcoin() + " | Loan: $" + sim.getLoanAmount() + RESET;
        }
        
		return output;
    }
}
