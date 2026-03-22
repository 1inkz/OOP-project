package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.location.LocationKey;
import simscli.pets.PetFactory;
import simscli.pets.PetType;
import simscli.sims.Sim;
import simscli.sims.SimType;

/**
 * Save/load round-trip tests for persistence integrity.
 */
public class SaveGameRoundTripTest {

    @BeforeEach
    public void setUp() {
        SaveGame.ensureSaveFileExists();
        SaveGame.clearSaveFile();
    }

    @AfterEach
    public void tearDown() {
        SaveGame.clearSaveFile();
    }

    @Test
    public void saveAndLoadRoundTripPreservesCoreState() {
        Game source = new Game();

        Sim ava = source.createSim("Ava", SimType.ADULT);
        source.createSim("Noah", SimType.CHILD);
        source.setActiveSim(0);

        source.travelTo(LocationKey.PARK);

        ava.setSimcoin(777);
        ava.setBankDeposit(123);
        ava.setLoanAmount(400);
        ava.adoptPet(PetFactory.create(PetType.CAT, "Milo"));

        SaveGame.saveGame(source);
        assertTrue(SaveGame.hasValidSaveData());

        Game loadedGame = new Game();
        boolean loaded = SaveGame.loadGame(loadedGame);

        assertTrue(loaded);
        assertEquals(2, loadedGame.sims().size());

        List<Sim> loadedSims = loadedGame.sims();
        Sim loadedAva = loadedSims.stream().filter(s -> s.getName().equals("Ava")).findFirst().orElseThrow();

        assertEquals("park", loadedAva.getLocation().key());
        assertEquals(777, loadedAva.getSimcoin());
        assertEquals(123, loadedAva.getBankDeposit());
        assertEquals(400, loadedAva.getLoanAmount());
        assertEquals(1, loadedAva.getPets().size());
        assertEquals("Milo", loadedAva.getPets().get(0).getName());

        source.shutdown();
        loadedGame.shutdown();
    }

    @Test
    public void emptySaveFileIsNotValidData() {
        SaveGame.clearSaveFile();
        assertFalse(SaveGame.hasValidSaveData());
    }
}
