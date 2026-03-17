package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.sims.Sim;

public final class Withdraw implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    String output = "";
    
    @Override public String name() { return "Withdraw Simcoin"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter
        
        if (sim.getBankDeposit() <= 0) {
        	output = RED + "No money available to withdraw!" + RESET;
        }
        else {
            int withdrawAmt = ui.intRange("Withdraw amount: $", 1, sim.getBankDeposit());

            if (sim.getBankingSystem().withdraw(withdrawAmt)) {
            	sim.earnSimcoin(withdrawAmt);
            	output = GREEN + "Withdrew $" + withdrawAmt + " | Simcoin: $" + sim.getSimcoin() + " | Deposit: $" + sim.getBankDeposit() + RESET;
            }
        }

		return output;
    }
}
