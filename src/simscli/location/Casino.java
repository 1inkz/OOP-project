package simscli.location;

import java.util.ArrayList;
import java.util.List;
import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Casino location: high-risk gambling with high-reward potential.
 *
 * <p>Players can use luck-based games such as Slot Machine and Blackjack to
 * potentially multiply their Simcoin, but may also lose their bet.</p>
 */
public final class Casino extends Location {
    @Override
    public String key() {
        return "casino";
    }

    @Override
    public String name() {
        return "Casino";
    }

    @Override
    public boolean canEnter(Sim sim) {
        return sim != null && sim.isAlive() && sim.getType() != SimType.CHILD;
    }

    @Override
    public List<Action> actions(Sim sim) {
        List<Action> actions = new ArrayList<>();
        actions.add(ActionFactory.create(ActionType.PLAY_SLOTS));
        actions.add(ActionFactory.create(ActionType.PLAY_BLACKJACK));
        actions.add(ActionFactory.create(ActionType.USE_TOILET));
        actions.add(ActionFactory.create(ActionType.EAT_SNACK));
        return actions;
    }

    @Override
    public String onEnter(Sim sim) {
        return sim.getName() + " stepped into the Casino. Fortune favors the bold.";
    }
}
