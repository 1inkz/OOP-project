package simscli.pets;

/**
 * Cat pet subclass.
 */
public final class Cat extends Pet {
    /**
     * Creates a cat with the given name.
     * @param name the cat's name
     */
    public Cat(String name) {
        super(name, PetType.CAT);
    }
}
