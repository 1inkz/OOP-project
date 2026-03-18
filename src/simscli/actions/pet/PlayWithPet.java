package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.NeedType;
import simscli.stats.Effect;

/**
 * Pet action: Play with a specific pet.
 */
public final class PlayWithPet implements Action {
    private final Pet pet;

    /**
     * Creates a play action for the specified pet.
     * @param pet the pet to play with (non-null)
     * @throws IllegalArgumentException if pet is null
     */
    public PlayWithPet(Pet pet) {
        if (pet == null) throw new IllegalArgumentException("Pet required");
        this.pet = pet;
    }

    @Override
    public String name() {
        return "Play with " + pet.getName();
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!pet.isAlive()) {
            return pet.getName() + " is no longer with us...";
        }

        pet.getHappiness().add(25);
        pet.getHunger().add(8);
        pet.getCleanliness().add(-10);
        pet.gainExperience(15);

        // Sim gets fun and energy boost
        sim.applyEffect(Effect.none()
                .plus(NeedType.FUN, +20)
                .plus(NeedType.ENERGY, -15)
                .plus(NeedType.HYGIENE, -5));

        return sim.getName() + " played with " + pet.getName() + ". Both are having a blast!";
    }
}
