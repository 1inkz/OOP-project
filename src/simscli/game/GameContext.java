package simscli.game;

import simscli.actions.ActionUIAdapter;

public final class GameContext implements ActionUIAdapter {
    private final Game game;

    public GameContext(Game game) {
    	this.game = game;
    }
    
    public Game game() {
        return game;
    }
    
    public GameClock getClock() {
        return game.getClock();
    }
    
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