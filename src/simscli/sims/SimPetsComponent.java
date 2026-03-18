package simscli.sims;

import java.util.ArrayList;
import java.util.List;
import simscli.game.GameLogger;
import simscli.pets.Pet;

/**
 * Manages Sim pets: adoption, aging, and death.
 * Encapsulates pet-related logic and lifecycle.
 */
public class SimPetsComponent {
    private final List<Pet> pets;
    private final String simName;
    private final GameLogger logger;

    public SimPetsComponent(String simName, GameLogger logger) {
        this.pets = new ArrayList<>();
        this.simName = simName;
        this.logger = logger;
    }

    public List<Pet> getPets() {
        return new ArrayList<>(pets);
    }

    /**
     * Adds a pet to this Sim's collection.
     * @param pet the Pet to adopt
     */
    public void adoptPet(Pet pet) {
        if (pet != null && !pets.contains(pet)) {
            pets.add(pet);
        }
    }

    /**
     * Updates all pets by one hour: aging and death checks.
     * Logs deaths and removes deceased pets.
     */
    public void updatePetsHourly() {
        List<Pet> deadPets = new ArrayList<>();
        for (Pet pet : pets) {
            if (pet.isAlive()) {
                pet.ageOneHour();
                if (!pet.isAlive()) {
                    deadPets.add(pet);
                }
            }
        }

        for (Pet pet : deadPets) {
            pets.remove(pet);
            logger.warn(simName + "'s beloved " + pet.getName() 
                    + " (" + pet.getType().getDisplayName() + ") has passed away...");
        }
    }
}
