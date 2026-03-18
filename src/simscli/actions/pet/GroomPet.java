package simscli.actions.pet;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Pet action: Groom a specific pet.
 */
public final class GroomPet implements Action {
    private final Pet pet;

    /**
     * Creates a groom action for the specified pet.
     * @param pet the pet to groom (non-null)
     * @throws IllegalArgumentException if pet is null
     */
    public GroomPet(Pet pet) {
        if (pet == null) throw new IllegalArgumentException("Pet required");
        this.pet = pet;
    }

    @Override
    public String name() {
        return "Groom " + pet.getName();
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!pet.isAlive()) {
            return pet.getName() + " is no longer with us...";
        }

        pet.getCleanliness().add(50);
        pet.getHappiness().add(15);
        pet.gainExperience(8);

        sim.applyEffect(Effect.none()
                .plus(NeedType.HYGIENE, +5));

        return sim.getName() + " groomed " + pet.getName() + ". " + pet.getName() + " looks fabulous!";
    }
}
