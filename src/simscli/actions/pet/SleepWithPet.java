package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;

/**
 * Pet action: Sleep with a specific pet.
 */
public final class SleepWithPet implements Action {
    private final Pet pet;

    /**
     * Creates a sleep action for the specified pet.
     * @param pet the pet to sleep with (non-null)
     * @throws IllegalArgumentException if pet is null
     */
    public SleepWithPet(Pet pet) {
        if (pet == null) throw new IllegalArgumentException("Pet required");
        this.pet = pet;
    }

    @Override
    public String name() {
        return "Sleep with " + pet.getName();
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!pet.isAlive()) {
            return pet.getName() + " is no longer with us...";
        }

        pet.getHappiness().add(30);
        pet.getHunger().add(-10);
        pet.getCleanliness().add(-5);
        pet.gainExperience(10);

        return sim.getName() + " slept with " + pet.getName() + ". " + pet.getName() + " is so content!";
    }
}
