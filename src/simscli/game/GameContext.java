package simscli.game;

import simscli.actions.ActionUIAdapter;

/**
 * Game context adapter providing actions with UI and game state access.
 * Implements ActionUIAdapter for dependency injection.
 */
public final class GameContext implements ActionUIAdapter {
    private final Game game;

    /**
     * Creates a game context wrapping the given Game instance.
     * @param game the Game object to provide access to
     */
    public GameContext(Game game) {
    	this.game = game;
    }
    
    public Game game() {
        return game;
    }
    
    /**
     * Gets the game clock.
     * @return the game's GameClock instance
     */
    public GameClock getClock() {
        return game.getClock();
    }
    
    /**
     * Resets the clock to the next day at midnight.
     */
    public void resetToNextDayMorning() {
        game.getClock().resetToNextDayMorning();
    }

    public void checkTimeRules() {
        game.checkTimeRules(true);
    }
    
    @Override
    public int intRange(String prompt, int min, int max) {
        return game.getUIInput().intRange(prompt, min, max);
    }
    
    @Override
    public String line(String prompt) {
        return game.getUIInput().line(prompt);
    }
}