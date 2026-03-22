package simscli.actions;

import simscli.actions.banking.*;
import simscli.actions.pet.*;
import simscli.actions.simple.*;
import simscli.pets.Pet;

/**
 * Factory for creating Action instances based on ActionType or Pet.
 * 
 * <p>Implements the Factory Pattern to encapsulate all action instantiation logic in one place
 * for maintainability and separation of concerns. Enables easy addition of new actions without
 * modifying existing code, adhering to the Open/Closed Principle.
 * 
 * <p>Usage:
 * <ul>
 *   <li>{@link #create(ActionType)} - Create a standard action by type</li>
 *   <li>{@link #createFeedPet(Pet)} - Create a pet-specific action</li>
 * </ul>
 */
public final class ActionFactory {
    private ActionFactory() {}

    /**
     * Creates an Action instance for the given action type.
     * @param type the ActionType to create
     * @return a new Action implementation
     * @throws IllegalArgumentException if action type is unknown
     */
    public static Action create(ActionType type) {
        switch (type) {
            case EAT_SNACK: return new EatSnack();
            case EAT_MEAL: return new EatMeal();
            case SLEEP: return new Sleep();
            case NAP: return new Nap();
            case SHOWER: return new Shower();
            case BRUSH_TEETH: return new BrushTeeth();
            case USE_TOILET: return new UseToilet();
            case WATCH_TV: return new WatchTV();
            case PLAY_GAME: return new PlayGame();
            case READ_BOOK: return new ReadBook();
            case EXERCISE: return new Exercise();
            case SOCIALISE: return new Socialise();
            case WORK: return new Work();
            case CLEAN_PUBLIC: return new Cleaning();
            case DEPOSIT: return new Deposit();
            case WITHDRAW: return new Withdraw();
            case APPLY_LOAN: return new ApplyLoan();
            case REPAY_LOAN: return new RepayLoan();
            case BUY_PET: return new BuyPet();
            case GET_CHECKUP: return new GetCheckup();
            default: throw new IllegalArgumentException("Unknown action: " + type);
        }
    }

    /**
     * Creates a FeedPet action for the specified pet.
     * @param pet the pet to feed
     * @return a new FeedPet action
     */
    public static Action createFeedPet(Pet pet) {
        return new FeedPet(pet);
    }

    /**
     * Creates a ShowerPet action for the specified pet.
     * @param pet the pet to shower
     * @return a new ShowerPet action
     */
    public static Action createShowerPet(Pet pet) {
        return new ShowerPet(pet);
    }

    /**
     * Creates a PlayWithPet action for the specified pet.
     * @param pet the pet to play with
     * @return a new PlayWithPet action
     */
    public static Action createPlayWithPet(Pet pet) {
        return new PlayWithPet(pet);
    }

    /**
     * Creates a SleepWithPet action for the specified pet.
     * @param pet the pet to sleep with
     * @return a new SleepWithPet action
     */
    public static Action createSleepWithPet(Pet pet) {
        return new SleepWithPet(pet);
    }

    /**
     * Creates a GroomPet action for the specified pet.
     * @param pet the pet to groom
     * @return a new GroomPet action
     */
    public static Action createGroomPet(Pet pet) {
        return new GroomPet(pet);
    }
}