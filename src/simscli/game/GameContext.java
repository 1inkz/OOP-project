package simscli.game;
import simscli.GameClock;

public final class GameContext {
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

}