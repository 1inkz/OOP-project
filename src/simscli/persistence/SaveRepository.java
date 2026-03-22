package simscli.persistence;

import simscli.game.Game;

/**
 * Port for game persistence operations.
 */
public interface SaveRepository {
    void ensureSaveFileExists();
    boolean hasValidSaveData();
    void saveGame(Game game);
    boolean loadGame(Game game);
    void clearSaveFile();
}
