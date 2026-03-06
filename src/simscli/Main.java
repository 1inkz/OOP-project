package simscli;

import simscli.game.Game;
import simscli.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        new ConsoleUI(game).run();
    }
}