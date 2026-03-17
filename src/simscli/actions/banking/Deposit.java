package simscli.actions.banking;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.sims.Sim;

public final class Deposit implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    String output = "";
    
    @Override public String name() { return "Deposit Simcoin"; }

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
