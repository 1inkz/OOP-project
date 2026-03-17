package simscli.actions;

import simscli.actions.banking.*;
import simscli.actions.pet.*;
import simscli.actions.simple.*;
import simscli.pets.Pet;

// Factory for creating Action instances based on ActionType or Pet. 
// Encapsulates all action instantiation logic in one place for maintainability and separation of concerns.
// This design allows for easy addition of new actions without modifying existing code, 
// adhering to the Open/Closed Principle.
public final class ActionFactory {
    private ActionFactory() {}

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

    public static Action createFeedPet(Pet pet) {
        return new FeedPet(pet);
    }

    public static Action createShowerPet(Pet pet) {
        return new ShowerPet(pet);
    }

    public static Action createPlayWithPet(Pet pet) {
        return new PlayWithPet(pet);
    }

    public static Action createSleepWithPet(Pet pet) {
        return new SleepWithPet(pet);
    }

    public static Action createGroomPet(Pet pet) {
        return new GroomPet(pet);
    }
}