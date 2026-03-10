package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.ui.Input;

public final class Withdraw implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private final Input in = new Input();
    String output = "";
    
    @Override public String name() { return "Withdraw Simcoin"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getBankDeposit() <= 0) {
        	output = RED + "No money available to withdraw!" + RESET;
        }
        else {
            int withdrawAmt = in.intRange("Withdraw amount: $", 1, sim.getBankDeposit());

            if (sim.getBankingSystem().withdraw(withdrawAmt)) {
            	sim.earnSimcoin(withdrawAmt);
            	output = GREEN + "Withdrew $" + withdrawAmt + " | Simcoin: $" + sim.getSimcoin() + " | Deposit: $" + sim.getBankDeposit() + RESET;
            }
        }

		return output;
    }
}