package simscli.game;

public final class GameContext {
    private final Game game;

    public GameContext(Game game) {
        this.game = game;
    }

    public Game game() {
        return game;
    }
}