package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class Bed implements Usable {
    @Override public String key() { return "bed"; }
    @Override public String name() { return "Bed"; }
    @Override public Action action() { return ActionFactory.create(ActionType.SLEEP); }
}