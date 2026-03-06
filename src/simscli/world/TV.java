package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class TV implements Usable {
    @Override public String key() { return "tv"; }
    @Override public String name() { return "Television"; }
    @Override public Action action() { return ActionFactory.create(ActionType.WATCH_TV); }
}