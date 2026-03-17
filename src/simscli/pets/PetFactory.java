package simscli.pets;

public final class PetFactory {
    private PetFactory() {}

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
