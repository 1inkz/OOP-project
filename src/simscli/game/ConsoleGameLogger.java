package simscli.game;

/**
 * Console-based implementation of GameLogger.
 * Outputs to System.out and System.err.
 */
public class ConsoleGameLogger implements GameLogger {
    /**
     * Logs info message to stdout.
     * @param message the message to log
     */
    @Override
    public void info(String message) {
        System.out.println(message);
    }

    /**
     * Logs warning message to stdout.
     * @param message the message to log
     */
    @Override
    public void warn(String message) {
        System.out.println(message);
    }

    /**
     * Logs error message to stderr.
     * @param message the message to log
     */
    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
