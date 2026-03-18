package simscli.world;

import simscli.actions.Action;
import simscli.actions.ActionFactory;
import simscli.actions.ActionType;

/**
 * World object: Bookshelf for reading.
 */
public final class Bookshelf implements Usable {
    @Override public String key() { return "bookshelf"; }
    @Override public String name() { return "Bookshelf"; }
    @Override public Action action() { return ActionFactory.create(ActionType.READ_BOOK); }
}