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
            default: throw new IllegalArgumentException("Unknown action: " + type);
        }
    }
}