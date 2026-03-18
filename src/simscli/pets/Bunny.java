package simscli.pets;

/**
 * Bunny pet subclass.
 */
public final class Bunny extends Pet {
    /**
     * Creates a bunny with the given name.
     * @param name the bunny's name
     */
    public Bunny(String name) {
        super(name, PetType.BUNNY);
    }
}
