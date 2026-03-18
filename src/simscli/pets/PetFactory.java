package simscli.pets;

/**
 * Factory for creating Pet instances by type.
 */
public final class PetFactory {
    private PetFactory() {}

    /**
     * Creates a pet of the specified type with the given name.
     * @param type the PetType to create
     * @param name the pet's name
     * @return a new Pet instance
     * @throws IllegalArgumentException if type is unknown
     */
    public static Pet create(PetType type, String name) {
        switch (type) {
            case DOG:
                return new Dog(name);
            case CAT:
                return new Cat(name);
            case BUNNY:
                return new Bunny(name);
            default:
                throw new IllegalArgumentException("Unknown pet type: " + type);
        }
    }
}
