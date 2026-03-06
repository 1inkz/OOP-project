package simscli.world;

import simscli.actions.Action;

public interface Usable {
    String key();      // command key like "fridge"
    String name();     // display name
    Action action();   // what action this object triggers
}