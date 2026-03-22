package simscli;

import simscli.game.Game;
import simscli.persistence.FileSaveRepository;
import simscli.persistence.SaveRepository;

/**
 * Handles game serialization and persistence.
 * Manages saving and loading Sim state to/from file.
 */
public final class SaveGame {
    private static final SaveRepository REPOSITORY = new FileSaveRepository();

    /**
     * Creates the save file if it doesn't exist.
     */
    public static void ensureSaveFileExists() {
        REPOSITORY.ensureSaveFileExists();
    }

    /**
     * Checks if a valid save file exists with data.
     * @return true if save file exists and is not empty
     */
    public static boolean hasValidSaveData() {
        return REPOSITORY.hasValidSaveData();
    }
    

    /**
     * Saves the current game state to file.
     * @param game the Game instance to save
     */
    public static void saveGame(Game game) {
        REPOSITORY.saveGame(game);
    }

    public static boolean loadGame(Game game) {
        return REPOSITORY.loadGame(game);
    }

    public static void clearSaveFile() {
        REPOSITORY.clearSaveFile();
    }
}
