package simscli.pets;

import simscli.stats.BoundedStat;

/**
 * Abstract base class for all pets.
 * Manages pet stats, experience, level, and serialization.
 */
public abstract class Pet {
    private final String name;
    private final PetType type;
    private final BoundedStat health = new BoundedStat(100);
    private final BoundedStat hunger = new BoundedStat(30);
    private final BoundedStat cleanliness = new BoundedStat(70);
    private final BoundedStat happiness = new BoundedStat(50);
    private int experience = 0;
    private int level = 1;
    private int age = 0;
    private boolean alive = true;

    /**
     * Creates a new Pet with the given name and type.
     * @param name the pet's name (non-empty)
     * @param type the pet's type (Dog, Cat, or Bunny)
     * @throws IllegalArgumentException if name is null or empty
     */
    protected Pet(String name, PetType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name required");
        }
        this.name = name.trim();
        this.type = type;
    }

    /**
     * Gets the pet's name.
     * @return the pet's name
     */
    public String getName() { return name; }
    
    /**
     * Gets the pet's type (Dog, Cat, or Bunny).
     * @return the pet's type
     */
    public PetType getType() { return type; }
    
    /**
     * Gets the health stat.
     * @return the health BoundedStat
     */
    public BoundedStat getHealth() { return health; }
    
    /**
     * Gets the hunger stat.
     * @return the hunger BoundedStat
     */
    public BoundedStat getHunger() { return hunger; }
    
    /**
     * Gets the cleanliness stat.
     * @return the cleanliness BoundedStat
     */
    public BoundedStat getCleanliness() { return cleanliness; }
    
    /**
     * Gets the happiness stat.
     * @return the happiness BoundedStat
     */
    public BoundedStat getHappiness() { return happiness; }
    
    /**
     * Gets the experience points.
     * @return the experience amount
     */
    public int getExperience() { return experience; }
    
    /**
     * Gets the current level.
     * @return the level
     */
    public int getLevel() { return level; }
    
    /**
     * Gets the age in hours.
     * @return the age
     */
    public int getAge() { return age; }
    
    /**
     * Checks if the pet is alive.
     * @return true if alive, false if dead
     */
    public boolean isAlive() { return alive; }

    /**
     * Sets the alive status.
     * @param alive true to alive, false for dead
     */
    public void setAlive(boolean alive) { this.alive = alive; }
    
    /**
     * Sets the experience points.
     * @param exp the new experience amount
     */
    public void setExperience(int exp) { this.experience = exp; }
    
    /**
     * Sets the level.
     * @param lvl the new level
     */
    public void setLevel(int lvl) { this.level = lvl; }
    
    /**
     * Sets the age.
     * @param a the new age
     */
    public void setAge(int a) { this.age = a; }

    /**
     * Awards experience to the pet, potentially triggering level up.
     * @param amount experience points to gain (must be positive)
     */
    public void gainExperience(int amount) {
        if (amount > 0) {
            experience += amount;
            checkLevelUp();
        }
    }

    private void checkLevelUp() {
        int expPerLevel = 100;
        int newLevel = ExperienceUtils.calculateLevel(experience, expPerLevel);
        if (newLevel > level) {
            level = newLevel;
            health.add(10);
            happiness.add(15);
        }
    }

    /**
     * Ages the pet by one in-game hour, increasing hunger and checking death conditions.
     */
    public void ageOneHour() {
        age++;
        hunger.add(5);
        happiness.add(-2);
        checkDeathConditions();
    }

    private void checkDeathConditions() {
        // Pet dies if health is 0
        if (health.isZero()) {
            alive = false;
        }
    }

    /**
     * Returns a formatted status summary of the pet's current state.
     * @return status string with name, type, level, and all stat values
     */
    public String getStatusSummary() {
        return String.format("%s (%s, Lvl %d) - Health: %d, Hunger: %d, Cleanliness: %d, Happiness: %d",
                name, type.getDisplayName(), level, health.get(), hunger.get(), cleanliness.get(), happiness.get());
    }

    /**
     * Serialize pet data to a string for saving (format: name-TYPE-health-hunger-cleanliness-happiness-exp-level-age-alive)
     * @return serialized pet data string
     */
    public String serialize() {
        return String.format("%s-%s-%d-%d-%d-%d-%d-%d-%d-%s",
                name.replace("-", "_"),
                type.name(),
                health.get(),
                hunger.get(),
                cleanliness.get(),
                happiness.get(),
                experience,
                level,
                age,
                alive ? "1" : "0"
        );
    }

    /**
     * Deserialize pet data from saved string
     * Format: name-TYPE-health-hunger-cleanliness-happiness-exp-level-age-alive
     * @param petData the serialized pet data string
     * @return the deserialized Pet object
     */
    public static Pet deserialize(String petData) {
        if (petData == null || petData.trim().isEmpty()) {
            return null;
        }

        try {
            // Split carefully - pet name is replaced "-" with "_", so we need fixed indices
            String[] parts = petData.split("-", 10); // Max 10 parts
            if (parts.length < 10) {
                return null; // Invalid pet data
            }

            String petName = parts[0].replace("_", "-");
            PetType petType = PetType.valueOf(parts[1]);
            int petHealth = Integer.parseInt(parts[2]);
            int petHunger = Integer.parseInt(parts[3]);
            int petCleanliness = Integer.parseInt(parts[4]);
            int petHappiness = Integer.parseInt(parts[5]);
            int petExp = Integer.parseInt(parts[6]);
            int petLevel = Integer.parseInt(parts[7]);
            int petAge = Integer.parseInt(parts[8]);
            boolean petAlive = parts[9].equals("1");

            Pet pet = PetFactory.create(petType, petName);
            pet.getHealth().set(petHealth);
            pet.getHunger().set(petHunger);
            pet.getCleanliness().set(petCleanliness);
            pet.getHappiness().set(petHappiness);
            pet.setExperience(petExp);
            pet.setLevel(petLevel);
            pet.setAge(petAge);
            pet.setAlive(petAlive);

            return pet;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Pet deserialization failed - invalid format: " + petData);
            return null;
        } catch (NumberFormatException e) {
            System.err.println("Pet deserialization failed - number parse error: " + petData);
            return null;
        } catch (IllegalArgumentException e) {
            System.err.println("Pet deserialization failed - invalid pet type: " + petData);
            return null;
        } catch (Exception e) {
            System.err.println("Pet deserialization failed: " + e.getMessage() + " for data: " + petData);
            return null;
        }
    }
}
