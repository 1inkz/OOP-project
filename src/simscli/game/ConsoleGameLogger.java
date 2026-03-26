package simscli.game;

/**
 * Console-based implementation of GameLogger.
 * Outputs to System.out and System.err.
 */
public class ConsoleGameLogger implements GameLogger {
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    /**
     * Logs info message to stdout.
     * @param message the message to log
     */
    @Override
    public void info(String message) {
        System.out.println(colorizeInfo(message));
    }

    /**
     * Logs warning message to stdout.
     * @param message the message to log
     */
    @Override
    public void warn(String message) {
        System.out.println(colorizeWarn(message));
    }

    /**
     * Logs error message to stderr.
     * @param message the message to log
     */
    @Override
    public void error(String message) {
        System.err.println(colorizeError(message));
    }

    private String colorizeInfo(String message) {
        if (isMidnightMessage(message)) {
            return BLUE + message + RESET;
        }
        return message;
    }

    private String colorizeWarn(String message) {
        if (isMidnightMessage(message)) {
            return BLUE + message + RESET;
        }
        return message;
    }

    private String colorizeError(String message) {
        if (isEliminationMessage(message)) {
            return RED + message + RESET;
        }
        return message;
    }

    private boolean isMidnightMessage(String message) {
        return message != null && message.toLowerCase().contains("midnight");
    }

    private boolean isEliminationMessage(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.contains("eliminated") || lower.contains("died from");
    }
}
