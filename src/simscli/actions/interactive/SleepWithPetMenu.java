package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.pet.SleepWithPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import java.util.List;

public final class SleepWithPetMenu implements Action {

    @Override
    public String name() {
        return "Sleep with Pets";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter
        List<Pet> pets = sim.getPets();

        if (pets.isEmpty()) {
            return "You don't have any pets to sleep with!";
        }

        System.out.println("\n===== Which Pet to Sleep With? =====");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            System.out.println((i + 1) + ") " + pet.getName() + " (" + pet.getType().getDisplayName() + ")");
        }
        System.out.println((pets.size() + 1) + ") All");
        System.out.println((pets.size() + 2) + ") Cancel");

        int choice = ui.intRange("\nSelect: ", 1, pets.size() + 2);

        if (choice == pets.size() + 2) {
            return "You decide to sleep alone for now.";
        }

        if (choice == pets.size() + 1) {
            // Sleep with all pets
            StringBuilder result = new StringBuilder(sim.getName() + " slept with all pets! ");
            
            for (Pet pet : pets) {
                if (pet.isAlive()) {
                    pet.getHappiness().add(30);
                    pet.getHunger().add(5);
                    pet.gainExperience(10);
                }
            }

            result.append("Everyone is so content!");
            return result.toString();
        }

        // Sleep with single pet
        Pet selectedPet = pets.get(choice - 1);
        return new SleepWithPet(selectedPet).perform(sim, ctx);
    }
}
