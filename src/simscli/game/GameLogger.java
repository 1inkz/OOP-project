package simscli.game;

/**
 * Interface for game logging/messaging.
 * Decouples game logic from UI concerns (System.out.println).
 * Facilitates testing and alternative output implementations.
 */
public interface GameLogger {
    /**
     * Logs an info message.
     * @param message the message to log
     */
    void info(String message);
    
    /**
     * Logs a warning message.
     * @param message the message to log
     */
    void warn(String message);
    
    /**
     * Logs an error message.
     * @param message the message to log
     */
    void error(String message);
}
