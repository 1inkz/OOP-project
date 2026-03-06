package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class Toilet implements Usable {
    @Override public String key() { return "toilet"; }
    @Override public String name() { return "Toilet"; }
    @Override public Action action() { return ActionFactory.create(ActionType.USE_TOILET); }
}