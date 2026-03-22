package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.request.ActionRequest;
import simscli.actions.request.SelectionActionRequest;
import simscli.actions.pet.GroomPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import java.util.List;

/**
 * Interactive action that lets the player choose which pet to groom.
 *
 * <p>Displays the Sim's pets, allows grooming a single pet or all pets, and
 * delegates single-pet grooming to {@link GroomPet}.</p>
 */

public final class GroomPetMenu implements Action {

    @Override
    public String name() {
        return "Groom Pet";
    }

    /**
     * Displays the pet-grooming menu and performs the selected grooming action.
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
            return "You don't have any pets to groom!";
        }

        System.out.println("\n===== Which Pet to Groom? =====");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            System.out.println((i + 1) + ") " + pet.getName() + " (" + pet.getType().getDisplayName() + ")");
        }
        System.out.println((pets.size() + 1) + ") All");
        System.out.println((pets.size() + 2) + ") Cancel");

        int choice = ui.intRange("\nSelect: ", 1, pets.size() + 2);
        return perform(sim, ctx, new SelectionActionRequest(choice));
    }

    @Override
    public String perform(Sim sim, GameContext ctx, ActionRequest request) {
        if (!(request instanceof SelectionActionRequest selectionRequest)) {
            return perform(sim, ctx);
        }

        List<Pet> pets = sim.getPets();
        int choice = selectionRequest.selection();

        if (choice < 1 || choice > pets.size() + 2) {
            return "Invalid pet selection.";
        }

        if (choice == pets.size() + 2) {
            return "You decide not to groom any pet for now.";
        }

        if (choice == pets.size() + 1) {
            // Groom all pets
            StringBuilder result = new StringBuilder(sim.getName() + " groomed all pets! ");
            
            for (Pet pet : pets) {
                if (pet.isAlive()) {
                    pet.getCleanliness().add(50);
                    pet.getHappiness().add(15);
                    pet.gainExperience(8);
                }
            }

            sim.applyEffect(Effect.none()
                    .plus(NeedType.HYGIENE, +5));

            result.append("Everyone looks fabulous!");
            return result.toString();
        }

        // Groom single pet
        Pet selectedPet = pets.get(choice - 1);
        return new GroomPet(selectedPet).perform(sim, ctx);
    }
}
