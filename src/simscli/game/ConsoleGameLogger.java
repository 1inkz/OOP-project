package simscli.game;

/**
 * Console-based implementation of GameLogger.
 * Outputs to System.out and System.err.
 */
public class ConsoleGameLogger implements GameLogger {
    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void warn(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
