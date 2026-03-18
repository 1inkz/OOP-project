package simscli.pets;

/**
 * Dog pet subclass.
 */
public final class Dog extends Pet {
    /**
     * Creates a dog with the given name.
     * @param name the dog's name
     */
    public Dog(String name) {
        super(name, PetType.DOG);
    }
}
