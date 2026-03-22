package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simscli.game.Game;
import simscli.game.SimManager;
import simscli.game.ConsoleGameLogger;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

public class SimManagerTest {
    private SimManager simManager;
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game(new ConsoleGameLogger());
        game.location().values().forEach(loc -> {}); 
        SaveGame.ensureSaveFileExists(); 
        simManager = new SimManager(game.getLogger());
    }

    @Test
    public void createSim_shouldAddToManagerAndSetAsActive() {
        Sim alice = simManager.createSim("Alice", SimType.ADULT, game);
        
        assertEquals(1, simManager.getAllSims().size());
        assertTrue(simManager.getAllSims().contains(alice));
        assertSame(alice, simManager.getActiveSim());
        assertEquals(0, simManager.getActiveSimIndex());
    }
    
    @Test
    public void setActiveSim_validIndex_shouldSwitchActiveSim() {
        simManager.createSim("Alice", SimType.ADULT, game);
        Sim bob = simManager.createSim("Bob", SimType.ELDER, game);
        
        simManager.setActiveSim(1);
        
        assertSame(bob, simManager.getActiveSim());
        assertEquals(1, simManager.getActiveSimIndex());
    }
    
    @Test
    public void setActiveSim_invalidIndex_shouldThrowException() {
        simManager.createSim("Alice", SimType.ADULT, game);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            simManager.setActiveSim(1);
        });
        assertEquals("Bad index: 1", exception.getMessage());
    }

    @Test
    public void removeDeadSims_shouldRemoveInactiveSims() {
        Sim alice = simManager.createSim("Alice", SimType.ADULT, game);
        Sim bob = simManager.createSim("Bob", SimType.ELDER, game);
        simManager.setActiveSim(1);
        bob.setAlive(false);
        
        simManager.removeDeadSims(game);
        
        assertEquals(1, simManager.getAllSims().size());
        assertTrue(simManager.getAllSims().contains(alice));    
    }
    
    @Test
    public void removeDeadSims_allDead_shouldClearManager() {
        Sim alice = simManager.createSim("Alice", SimType.ADULT, game);
        Sim bob = simManager.createSim("Bob", SimType.ELDER, game);
        alice.setAlive(false);
        bob.setAlive(false);
        
        simManager.removeDeadSims(game);
        
        assertTrue(simManager.getAllSims().isEmpty());
        assertNull(simManager.getActiveSim());
        assertEquals(-1, simManager.getActiveSimIndex());
    }
    
    private String getZeroNeedReason(Sim sim) {
        try {
            var method = SimManager.class.getDeclaredMethod("getZeroNeedReason", Sim.class);
            method.setAccessible(true);
            return (String) method.invoke(simManager, sim);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
            return "";
        }
    }
    
    @Test
    public void getZeroNeedReason_hungerZero_shouldReturnHunger() {
        Sim alice = simManager.createSim("Alice", SimType.ADULT, game);
        alice.getNeeds().set(NeedType.HUNGER, 0);
        alice.setAlive(false);
        
        simManager.removeDeadSims(game); 
        
        String deathReason = getZeroNeedReason(alice);
        assertEquals("HUNGER", deathReason);
    }

 }