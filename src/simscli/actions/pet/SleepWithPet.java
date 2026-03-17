package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;

public final class SleepWithPet implements Action {
    private final Pet pet;

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
        pet.getHunger().add(5);
        pet.gainExperience(10);

        return sim.getName() + " slept with " + pet.getName() + ". " + pet.getName() + " is so content!";
    }
}
