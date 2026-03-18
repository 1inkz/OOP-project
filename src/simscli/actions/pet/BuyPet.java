package simscli.actions.pet;

import simscli.actions.Action;
import simscli.actions.ActionUIAdapter;
import simscli.game.GameContext;
import simscli.pets.Pet;
import simscli.pets.PetFactory;
import simscli.pets.PetType;
import simscli.sims.Sim;

/**
 * Pet action: Buy a pet from the pet store.
 */
public final class BuyPet implements Action {
    @Override
    public String name() {
        return "Buy Pet";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        ActionUIAdapter ui = ctx;  // GameContext implements ActionUIAdapter
        
        System.out.println("\n===== Pet Store =====");
        System.out.println("Available Pets:");
        int index = 1;
        for (PetType type : PetType.values()) {
            System.out.println(index + ". " + type.getDisplayName() + " ($" + type.getPrice() + ")");
            index++;
        }
        System.out.println(index + ". Cancel");

        int choice = ui.intRange("\nSelect pet to buy (1-" + index + "): ", 1, index);

        if (choice == index) {
            return "You leave the pet store.";
        }

        PetType[] types = PetType.values();
        PetType petType = types[choice - 1];

        if (sim.getSimcoin() < petType.getPrice()) {
            return "Not enough Simcoin! " + petType.getDisplayName() + " costs $" + petType.getPrice() +
                    " but you only have $" + sim.getSimcoin();
        }

        System.out.print("Enter pet name: ");
        String petName = ui.line("");

        if (petName.trim().isEmpty()) {
            return "Pet name cannot be empty!";
        }

        Pet pet = PetFactory.create(petType, petName);
        sim.adoptPet(pet);
        sim.addSimcoin(-petType.getPrice());

        return sim.getName() + " bought a " + petType.getDisplayName() + " and named it " + petName + "!";
    }
}
