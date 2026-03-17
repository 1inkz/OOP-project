package simscli.game;

import java.util.*;
import simscli.SaveGame;
import simscli.actions.Action;
import simscli.jobs.JobFactory;
import simscli.location.Location;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;
import simscli.ui.Input;

/**
 * Main Game orchestrator: delegates to focused managers for clean architecture.
 * Follows facade pattern - coordinates multiple managers without mixing concerns.
 * 
 * Managers:
 *  - SimManager: Sim lifecycle (creation, tracking, removal)
 *  - LocationManager: Locations and travel
 *  - TimeManager: Game clock and time advancement
 *  - ActionExecutor: Action performance and job changes
 *  - LoanManager: Loan overdues and repossessions
 */
public final class Game {
    private final SimManager simManager;
    private final LocationManager locationManager;
    private final TimeManager timeManager;
    private final ActionExecutor actionExecutor;
    private final LoanManager loanManager;
    private final GameLogger logger;
    
    private final int gameStartDay;
    private Input uiInput;

    public Game() {
        this(new ConsoleGameLogger());
    }

    public Game(GameLogger logger) {
        this.logger = logger;
        
        // Initialize managers
        this.simManager = new SimManager(logger);
        this.locationManager = new LocationManager(logger);
        this.timeManager = new TimeManager(new GameClock(1, 480), logger);
        this.actionExecutor = new ActionExecutor(logger);
        this.loanManager = new LoanManager(logger);
        
        // Initialize game state
        this.gameStartDay = timeManager.getClock().getDayNumber();
        this.locationManager.initialize();
    }

    // ==================== Lifecycle ====================
    
    public void resetGame() {
        simManager.clearSimList();
        timeManager.getClock().resetNewGame();
    }

    public void shutdown() {
        timeManager.shutdown();
    }

    public int getGameStartDay() {
        return gameStartDay;
    }

    // ==================== Sim Management ====================
    
    public List<Sim>sims() {
        return simManager.getAllSims();
    }

    public Sim activeSim() {
        return simManager.getActiveSim();
    }

    public void setActiveSim(int index) {
        simManager.setActiveSim(index);
    }

    public int getActiveSimIndex() {
        return simManager.getActiveSimIndex();
    }

    public void addSim(Sim sim) {
        simManager.addSim(sim);
    }

    public void clearSimList() {
        simManager.clearSimList();
    }

    public Sim createSim(String name, SimType type) {
        return simManager.createSim(name, type, this);
    }

    public void cleanupDeadSims() {
        simManager.removeDeadSims(this);
    }

    // ==================== Time Management ====================
    
    public String timeString() {
        return timeManager.getTimeString();
    }

    public GameClock getClock() {
        return timeManager.getClock();
    }

    public void setClock(GameClock clock) {
        timeManager.setClock(clock);
    }

    public void advanceTimeForAction() {
        timeManager.advanceTimeForAction(simManager.getAllSims(), simManager.getActiveSimIndex(), this);
        loanManager.checkLoanOverdueRules(simManager.getAllSims(), simManager.getActiveSim(), this);
        simManager.removeDeadSims(this);
    }

    public void autoAdvanceRealTime(double deltaSeconds) {
        timeManager.autoAdvanceRealTime(deltaSeconds, simManager.getAllSims(), this);
    }

    public void checkTimeRules(boolean actionTriggered) {
        timeManager.checkTimeRules(actionTriggered, simManager.getAllSims());
    }

    // ==================== Location Management ====================
    
    public Map<String, Location> location() {
        return locationManager.getLocations();
    }

    public String travelTo(String destinationKey) {
        Sim active = simManager.getActiveSim();
        if (active == null) return "No active sim.";
        
        String result = locationManager.travelTo(active, destinationKey);
        return result;
    }

    public String performLocationAction(int actionIndex) {
        Sim active = simManager.getActiveSim();
        if (active == null) return "No active sim.";
        
        return locationManager.performLocationAction(active, actionIndex);
    }

    public String useObject(String objectKey) {
        return locationManager.useObject(objectKey);
    }

    // ==================== Action Execution ====================
    
    public String performAction(Action action) {
        Sim active = simManager.getActiveSim();
        String result = actionExecutor.performAction(active, action, this);
        
        simManager.removeDeadSims(this);
        return result;
    }

    public String changeJob(String jobName) {
        return actionExecutor.changeJob(simManager.getActiveSim(), jobName);
    }

    // ==================== UI Input ====================
    
    public void setUIInput(Input input) {
        this.uiInput = input;
    }

    public Input getUIInput() {
        return uiInput;
    }

    // ==================== Utility ====================
    
    public GameLogger getLogger() {
        return logger;
    }
}
