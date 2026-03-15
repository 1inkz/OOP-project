package simscli.location;

import java.util.ArrayList;
import java.util.List;
import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;

public final class Bank extends Location {
    @Override public String key() { return "bank"; }
    @Override public String name() { return "Bank"; }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>();

        if (sim != null && sim.getJob().canWork() && canWorkHere(sim)) {
            actions.add(ActionFactory.create(ActionType.WORK));
        }

        actions.add(ActionFactory.create(ActionType.DEPOSIT));

        if (sim.getBankDeposit() > 0) {
            actions.add(ActionFactory.create(ActionType.WITHDRAW));
        }

        actions.add(ActionFactory.create(ActionType.APPLY_LOAN));

        if (sim.getLoanAmount() > 0) {
            actions.add(ActionFactory.create(ActionType.REPAY_LOAN));
        }

        return actions;
    }

    private boolean canWorkHere(Sim sim) {
        for (String location : sim.getJob().getWorkLocations()) {
            if (this.key().equalsIgnoreCase(location)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String onEnter(simscli.sims.Sim sim) {
        return sim.getName() + " entered the bank.";
    }
}
