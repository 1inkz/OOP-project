package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class Treadmill implements Usable {
    @Override public String key() { return "treadmill"; }
    @Override public String name() { return "Treadmill"; }
    @Override public Action action() { return ActionFactory.create(ActionType.EXERCISE); }
}