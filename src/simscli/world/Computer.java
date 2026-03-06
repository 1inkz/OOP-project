package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

public final class Computer implements Usable {
    @Override public String key() { return "computer"; }
    @Override public String name() { return "Computer"; }
    @Override public Action action() { return ActionFactory.create(ActionType.PLAY_GAME); }
}