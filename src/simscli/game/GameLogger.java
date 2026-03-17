package simscli.game;

/**
 * Interface for game logging/messaging.
 * Decouples game logic from UI concerns (System.out.println).
 * Facilitates testing and alternative output implementations.
 */
public interface GameLogger {
    void info(String message);
    void warn(String message);
    void error(String message);
}
