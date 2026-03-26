package simscli.location;

import java.util.ArrayList;
import java.util.List;
import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;

/**
 * Bank location: offers financial operations and work for Bank Tellers.
 */
public final class Bank extends Location {
    @Override public String key() { return "bank"; }
    @Override public String name() { return "Bank"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>();
        
    if (sim.getSimcoin() > 0) {
        	actions.add(ActionFactory.create(ActionType.DEPOSIT));
        }
        
        if (sim.getBankDeposit() > 0) {
            actions.add(ActionFactory.create(ActionType.WITHDRAW));
        }

        if (sim.getLoanAmount() < simscli.bank.BankingSystem.getLoanLimit()) {
        	actions.add(ActionFactory.create(ActionType.APPLY_LOAN));
        }

        if (sim.getLoanAmount() > 0) {
            actions.add(ActionFactory.create(ActionType.REPAY_LOAN));
        }

        return actions;
    }

    @Override
    public String onEnter(simscli.sims.Sim sim) {
        return sim.getName() + " entered the bank.";
    }
}
