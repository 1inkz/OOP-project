package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.pet.ShowerPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import java.util.List;

/**
 * Interactive action that lets the player choose which pet to shower.
 *
 * <p>Displays the Sim's pets, allows showering a single pet or all pets,
 * and delegates single-pet showering to {@link ShowerPet}.</p>
 */

public final class ShowerPetMenu implements Action {

    @Override
    public String name() {
        return "Shower Pets";
    }

    /**
     * Displays the pet-showering menu and performs the selected action.
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
            return "You don't have any pets to shower!";
        }

        System.out.println("\n===== Which Pet to Shower? =====");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            System.out.println((i + 1) + ") " + pet.getName() + " (" + pet.getType().getDisplayName() + ")");
        }
        System.out.println((pets.size() + 1) + ") All");
        System.out.println((pets.size() + 2) + ") Cancel");

        int choice = ui.intRange("\nSelect: ", 1, pets.size() + 2);

        if (choice == pets.size() + 2) {
            return "You decide not to shower any pet for now.";
        }

        if (choice == pets.size() + 1) {
            // Shower all pets
            StringBuilder result = new StringBuilder(sim.getName() + " showered all pets! ");
            
            for (Pet pet : pets) {
                if (pet.isAlive()) {
                    pet.getCleanliness().add(40);
                    pet.getHappiness().add(-5);
                    pet.gainExperience(3);
                }
            }

            sim.applyEffect(Effect.none()
                    .plus(NeedType.HYGIENE, +5)
                    .plus(NeedType.FUN, +3));

            result.append("Everyone is squeaky clean!");
            return result.toString();
        }

        // Shower single pet
        Pet selectedPet = pets.get(choice - 1);
        return new ShowerPet(selectedPet).perform(sim, ctx);
    }
}
