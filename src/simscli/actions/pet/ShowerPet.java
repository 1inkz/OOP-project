package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.NeedType;
import simscli.stats.Effect;

/**
 * Pet action: Shower a specific pet.
 */
public final class ShowerPet implements Action {
    private final Pet pet;

    /**
     * Creates a shower action for the specified pet.
     * @param pet the pet to shower (non-null)
     * @throws IllegalArgumentException if pet is null
     */
    public ShowerPet(Pet pet) {
        if (pet == null) throw new IllegalArgumentException("Pet required");
        this.pet = pet;
    }

    @Override
    public String name() {
        return "Shower " + pet.getName();
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!pet.isAlive()) {
            return pet.getName() + " is no longer with us...";
        }

        pet.getCleanliness().add(40);
        pet.getHappiness().add(-5);
        pet.gainExperience(3);

        // Sim also gets hygiene boost from spending time with pet
        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +5)
                .plus(NeedType.FUN, +3));

        return sim.getName() + " showered " + pet.getName() + ". " + pet.getName() + " is now squeaky clean!";
    }
}
