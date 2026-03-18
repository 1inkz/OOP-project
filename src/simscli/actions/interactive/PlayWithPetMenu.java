package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.pet.PlayWithPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import java.util.List;

/**
 * Interactive action that lets the player choose which pet to play with.
 *
 * <p>Displays the Sim's pets, allows playing with a single pet or all pets,
 * and delegates single-pet play to {@link PlayWithPet}.</p>
 */

public final class PlayWithPetMenu implements Action {

    @Override
    public String name() {
        return "Play with Pets";
    }

    /**
     * Displays the pet-play menu and performs the selected action.
     *
     * @param sim the Sim whose pets are being managed
     * @param ctx the active game context, used to collect user input
     * @return a message describing the outcome of the selected action
     */

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter
        List<Pet> pets = sim.getPets();

        if (pets.isEmpty()) {
            return "You don't have any pets to play with!";
        }

        System.out.println("\n===== Which Pet to Play With? =====");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            System.out.println((i + 1) + ") " + pet.getName() + " (" + pet.getType().getDisplayName() + ")");
        }
        System.out.println((pets.size() + 1) + ") All");
        System.out.println((pets.size() + 2) + ") Cancel");

        int choice = ui.intRange("\nSelect: ", 1, pets.size() + 2);

        if (choice == pets.size() + 2) {
            return "You decide to play with something else for now.";
        }

        if (choice == pets.size() + 1) {
            // Play with all pets
            StringBuilder result = new StringBuilder(sim.getName() + " played with all pets! ");
            
            for (Pet pet : pets) {
                if (pet.isAlive()) {
                    pet.getHappiness().add(25);
                    pet.getHunger().add(8);
                    pet.getCleanliness().add(-10);
                    pet.gainExperience(15);
                }
            }

            sim.applyEffect(Effect.none()
                    .plus(NeedType.FUN, +20)
                    .plus(NeedType.ENERGY, -15)
                    .plus(NeedType.HYGIENE, -5));

            result.append("Everyone had a blast!");
            return result.toString();
        }

        // Play with single pet
        Pet selectedPet = pets.get(choice - 1);
        return new PlayWithPet(selectedPet).perform(sim, ctx);
    }
}
