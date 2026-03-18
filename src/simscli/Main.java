package simscli;

import simscli.game.Game;
import simscli.ui.ConsoleUI;


/**
 * Entry point for the Sims simulation game.
 * Initializes the game and starts the console UI.
 */
public class Main {
    /**
     * Starts the game.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Game game = new Game();
        ExitGuard.install(game);
        new ConsoleUI(game).run();
    }
}