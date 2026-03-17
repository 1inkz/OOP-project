package simscli.actions.interactive;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.actions.pet.GroomPet;
import simscli.pets.Pet;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import java.util.List;

public final class GroomPetMenu implements Action {

    @Override
    public String name() {
        return "Groom Pet";
    }

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
