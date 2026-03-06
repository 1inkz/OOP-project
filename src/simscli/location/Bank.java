package simscli.location;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

import java.util.Arrays;
import java.util.List;

public final class Bank extends Location {
    @Override public String key() { return "bank"; }
    @Override public String name() { return "Bank"; }

    @Override
    public List<Action> actions() {
        // Keep it simple: bank is mostly "work" and (optional) later add deposit/withdraw actions.
        return Arrays.asList(
                ActionFactory.create(ActionType.WORK)
        );
    }

    @Override
    public String onEnter(simscli.sims.Sim sim) {
        return sim.getName() + " entered the bank. The air smells like paperwork.";
    }
}