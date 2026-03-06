package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class ShowerStall implements Usable {
    @Override public String key() { return "shower"; }
    @Override public String name() { return "Shower"; }
    @Override public Action action() { return ActionFactory.create(ActionType.SHOWER); }
}