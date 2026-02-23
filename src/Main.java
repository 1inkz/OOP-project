import location.*;
import sim.Sim;
import ui.ConsoleUI;
import trait.*;
import world.World;

public static void main(String[] args) {
    World world = new World();
    world.addLocation(new Home());
    world.addLocation(new Bank());

    Sim sim = new Sim("Hanry", 50);
    sim.addTrait(new LazyTrait());

    new ConsoleUI().run(sim, world);
}