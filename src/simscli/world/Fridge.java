package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

/**
 * World object: Fridge for eating meals.
 */
public final class Fridge implements Usable {
    @Override public String key() { return "fridge"; }
    @Override public String name() { return "Fridge"; }
    @Override public Action action() { return ActionFactory.create(ActionType.EAT_MEAL); }
}