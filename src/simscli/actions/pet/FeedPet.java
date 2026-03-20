package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;

/**
 * Pet action: Feed a specific pet.
 */
public final class FeedPet implements Action {
    private final Pet pet;

    /**
     * Creates a feed action for the specified pet.
     * @param pet the pet to feed (non-null)
     * @throws IllegalArgumentException if pet is null
     */
    public FeedPet(Pet pet) {
        if (pet == null) throw new IllegalArgumentException("Pet required");
        this.pet = pet;
    }

    @Override
    public String name() {
        return "Feed " + pet.getName();
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!pet.isAlive()) {
            return pet.getName() + " is no longer with us...";
        }

        pet.getHunger().add(40);
        pet.getHappiness().add(10);
        pet.gainExperience(5);

        return sim.getName() + " fed " + pet.getName() + ". " + pet.getName() + " is feeling happy from a wonderful meal!";
    }
}
