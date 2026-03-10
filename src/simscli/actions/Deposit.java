package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.ui.Input;

public final class Deposit implements Action {
    
    private static final String RED = "\u001B[31m";       
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
    private final Input in = new Input();
    String output = "";
    
    @Override public String name() { return "Deposit Simcoin"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getSimcoin() <= 0) {
        	output = RED + "You have no Simcoin to deposit!" + RESET;
        }
        else {
            int depositAmt = in.intRange("Deposit amount: $", 1, sim.getSimcoin());

            if (sim.getBankingSystem().deposit(depositAmt)) {
            	sim.spendSimcoin(depositAmt);
            	output = GREEN + "Deposited $" + depositAmt + " | Simcoin: $" + sim.getSimcoin() +" | Deposit: $" + sim.getBankDeposit() + RESET;
            }
        }
		return output;
    }
}