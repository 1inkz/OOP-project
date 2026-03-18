package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.pet.FeedPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import java.util.List;

/**
 * Interactive action that lets the player choose which pet to feed.
 *
 * <p>Displays the Sim's pets, allows feeding a single pet or all pets, and
 * delegates single-pet feeding to {@link FeedPet}.</p>
 */

public final class FeedPetMenu implements Action {

    @Override
    public String name() {
        return "Feed Pets";
    }

    /**
     * Displays the pet-feeding menu and performs the selected feeding action.
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
            return "You don't have any pets to feed!";
        }

        System.out.println("\n===== Which Pet to Feed? =====");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            System.out.println((i + 1) + ") " + pet.getName() + " (" + pet.getType().getDisplayName() + ")");
        }
        System.out.println((pets.size() + 1) + ") All");
        System.out.println((pets.size() + 2) + ") Cancel");

        int choice = ui.intRange("\nSelect: ", 1, pets.size() + 2);

        if (choice == pets.size() + 2) {
            return "You decide not to feed any pet for now.";
        }

        if (choice == pets.size() + 1) {
            // Feed all pets
            StringBuilder result = new StringBuilder(sim.getName() + " fed all pets! ");
            
            for (Pet pet : pets) {
                if (pet.isAlive()) {
                    pet.getHunger().add(-30);
                    pet.getHappiness().add(10);
                    pet.gainExperience(5);
                }
            }

            result.append("Everyone is happy!");
            return result.toString();
        }

        // Feed single pet
        Pet selectedPet = pets.get(choice - 1);
        return new FeedPet(selectedPet).perform(sim, ctx);
    }
}
