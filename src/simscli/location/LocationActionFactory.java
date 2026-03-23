package simscli.location;

import simscli.actions.*;
import simscli.actions.banking.*;
import simscli.actions.interactive.*;
import simscli.actions.simple.*;
import simscli.actions.pet.BuyPet;

/**
 * Factory for creating location-specific actions.
 * Decouples locations from action implementation details.
 * Follows Dependency Inversion Principle.
 */
public final class LocationActionFactory {
    public enum LocationActionType {
        // Simple actions
        SLEEP, EAT_SNACK, EAT_MEAL, DINE_OUT, USE_TOILET, SHOWER, EXERCISE, NAP,
        WATCH_TV, READ_BOOK, PLAY_GAME, SOCIALISE, BRUSH_TEETH, CLEANING,
        WORK, DEPOSIT, WITHDRAW, APPLY_LOAN, REPAY_LOAN,
        
        // Hospital specific
        GET_CHECKUP,
        
        // Pet actions (menu-driven)
        FEED_PET_MENU, SHOWER_PET_MENU, PLAY_WITH_PET_MENU, 
        SLEEP_WITH_PET_MENU, GROOM_PET_MENU,
        
        // Shop actions
        BUY_PET
    }
    
    /**
     * Creates a location action by type.
     * @param type the action type to instantiate
     * @return a new Action instance
     * @throws IllegalArgumentException if type is unknown
     */
    public static Action create(LocationActionType type) {
        switch (type) {
            case SLEEP: return new Sleep();
            case EAT_SNACK: return new EatSnack();
            case EAT_MEAL: return new EatMeal();
            case DINE_OUT: return new DineOut();
            case USE_TOILET: return new UseToilet();
            case SHOWER: return new Shower();
            case EXERCISE: return new Exercise();
            case NAP: return new Nap();
            case WATCH_TV: return new WatchTV();
            case READ_BOOK: return new ReadBook();
            case PLAY_GAME: return new PlayGame();
            case SOCIALISE: return new Socialise();
            case BRUSH_TEETH: return new BrushTeeth();
            case CLEANING: return new Cleaning();
            case WORK: return new Work();
            case DEPOSIT: return new Deposit();
            case WITHDRAW: return new Withdraw();
            case APPLY_LOAN: return new ApplyLoan();
            case REPAY_LOAN: return new RepayLoan();
            case GET_CHECKUP: return new GetCheckup();
            case FEED_PET_MENU: return new FeedPetMenu();
            case SHOWER_PET_MENU: return new ShowerPetMenu();
            case PLAY_WITH_PET_MENU: return new PlayWithPetMenu();
            case SLEEP_WITH_PET_MENU: return new SleepWithPetMenu();
            case GROOM_PET_MENU: return new GroomPetMenu();
            case BUY_PET: return new BuyPet();
            default: throw new IllegalArgumentException("Unknown action type: " + type);
        }
    }
}
