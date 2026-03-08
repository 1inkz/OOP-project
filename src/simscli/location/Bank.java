package simscli.location;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Bank extends Location {
    @Override public String key() { return "bank"; }
    @Override public String name() { return "Bank"; }

    @Override
    public List<Action> actions(Sim sim) {
        // Keep it simple: bank is mostly "work" and (optional) later add deposit/withdraw actions.
    	List<Action> actions = new ArrayList<>();
        if (sim != null && sim.getJob().canWork() 
                && sim.getJob().getWorkLocation().equals(this.key())) {
            actions.add(ActionFactory.create(ActionType.WORK));
        }
        return actions;
    }

    @Override
    public String onEnter(simscli.sims.Sim sim) {
        return sim.getName() + " entered the bank. The air smells like paperwork.";
    }
}