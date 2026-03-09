package simscli.actions;

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
            case CLEAN_PUBLIC: return new CleanPublic();
            case DEPOSIT: return new Deposit();
            case WITHDRAW: return new Withdraw();
            case APPLY_LOAN: return new ApplyLoan();
            case REPAY_LOAN: return new RepayLoan();
            default: throw new IllegalArgumentException("Unknown action: " + type);
        }
    }
}