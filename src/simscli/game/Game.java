package simscli.game;

import java.util.*;
import simscli.actions.Action;
import simscli.actions.request.ActionRequest;
import simscli.location.Location;
import simscli.location.LocationKey;
import simscli.sims.Sim;
import simscli.sims.SimType;
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
    private final ActionFlavorService flavorService;
    private boolean isGameModified = false;
    
    private final int gameStartDay;
    private Input uiInput;

    /**
     * Initializes the Game with default console logger.
     */
    public Game() {
        this(new ConsoleGameLogger());
    }

    /**
     * Initializes the Game with a custom logger for event tracking.
     * @param logger the GameLogger instance for logging game events
     */
    public Game(GameLogger logger) {
        this.logger = logger;
        
        // Initialize managers
        this.simManager = new SimManager(logger);
        this.locationManager = new LocationManager(logger);
        this.timeManager = new TimeManager(new GameClock(1, 480), logger);
        this.actionExecutor = new ActionExecutor();
        this.loanManager = new LoanManager(logger);
        this.flavorService = new ActionFlavorService();
        
        // Initialize game state
        this.gameStartDay = timeManager.getClock().getDayNumber();
        this.locationManager.initialize();
        
        // Inject Game instance into LocationManager after full initialization
        this.locationManager.setGame(this);
    }
    
    // ==================== SAVE GAME ====================
    /**
     * Check if game is modify for save game features
     */    
    public boolean isGameModified() {
        return isGameModified;
    }
    
    public void markGameModified() {
        this.isGameModified = true;
    }

    // ==================== Lifecycle ====================
    
    /**
     * Resets the game state, clearing all Sims and resetting the game clock to start.
     */
    public void resetGame() {
        simManager.clearSimList();
        timeManager.getClock().resetNewGame();
    }

    /**
     * Shuts down the game, stopping all background operations.
     */
    public void shutdown() {
        timeManager.shutdown();
    }

    /**
     * Gets the day number when the game started.
     * @return the starting day of this game session
     */
    public int getGameStartDay() {
        return gameStartDay;
    }

    // ==================== Sim Management ====================
    
    /**
     * Gets all Sims in the game.
     * @return list of all Sims
     */
    public List<Sim>sims() {
        return simManager.getAllSims();
    }

    /**
     * Gets the currently active Sim being played.
     * @return the active Sim or null if none is selected
     */
    public Sim activeSim() {
        return simManager.getActiveSim();
    }

    /**
     * Sets which Sim is currently active by index.
     * @param index the index in the Sim list (0-based)
     */
    public void setActiveSim(int index) {
        simManager.setActiveSim(index);
    }

    /**
     * Gets the index of the currently active Sim.
     * @return the active Sim's index or -1 if none
     */
    public int getActiveSimIndex() {
        return simManager.getActiveSimIndex();
    }

    /**
     * Adds a Sim to the game.
     * @param sim the Sim to add
     */
    public void addSim(Sim sim) {
        simManager.addSim(sim);
    }

    /**
     * Removes all Sims from the game.
     */
    public void clearSimList() {
        simManager.clearSimList();
    }

    /**
     * Creates a new Sim with given name and type.
     * @param name the Sim's name
     * @param type the Sim's type (Adult, Child, Elder)
     * @return the newly created Sim
     */
    public Sim createSim(String name, SimType type) {
        return simManager.createSim(name, type, this);
    }

    /**
     * Removes all dead Sims from the game.
     */
    public void cleanupDeadSims() {
        simManager.removeDeadSims(this);
    }

    // ==================== Time Management ====================
    
    /**
     * Gets the current time as a formatted string.
     * @return formatted time string (e.g., "Day 5 - 8:30 AM")
     */
    public String timeString() {
        return timeManager.getTimeString();
    }

    /**
     * Gets the game clock.
     * @return the GameClock instance
     */
    public GameClock getClock() {
        return timeManager.getClock();
    }

    /**
     * Sets the game clock.
     * @param clock the new GameClock to use
     */
    public void setClock(GameClock clock) {
        timeManager.setClock(clock);
    }

    /**
     * Advances game time after an action, checking loan rules and removing dead Sims.
     */
    public void advanceTimeForAction() {
        timeManager.advanceTimeForAction(simManager.getAllSims(), simManager.getActiveSimIndex(), this);
        loanManager.checkLoanOverdueRules(simManager.getAllSims(), simManager.getActiveSim(), this);
        simManager.removeDeadSims(this);
    }

    /**
     * Automatically advances real-time game progression.
     * @param deltaSeconds the elapsed real time in seconds
     */
    public void autoAdvanceRealTime(double deltaSeconds) {
        timeManager.autoAdvanceRealTime(deltaSeconds, simManager.getAllSims(), this);
    }

    /**
     * Checks and applies time-based rules (events, status changes, etc.).
     * @param actionTriggered whether an action was just performed
     */
    public void checkTimeRules() {
        timeManager.checkTimeRules(simManager.getAllSims(), this);
    }

    // ==================== Location Management ====================
    
    /**
     * Gets all locations in the game world.
     * @return map of location keys to Location objects
     */
    public Map<String, Location> location() {
        return locationManager.getLocations();
    }

    /**
     * Moves the active Sim to a destination location.
     * @param destinationKey the key of the destination location
     * @return message describing the travel result
     */
    public String travelTo(String destinationKey) {
        try {
            return travelTo(LocationKey.fromKey(destinationKey));
        } catch (IllegalArgumentException e) {
            return "Unknown location. Try: " + locationManager.getLocations().keySet();
        }
    }

    /**
     * Moves the active Sim to a typed destination location.
     * @param destination the destination location key
     * @return message describing the travel result
     */
    public String travelTo(LocationKey destination) {
        Sim active = simManager.getActiveSim();
        if (active == null) return "No active sim.";

        String result = locationManager.travelTo(active, destination);
        return flavorService.decorate(result, active, timeManager.getClock());
    }

    /**
     * Performs an action available at the current location.
     * @param actionIndex the index of the action to perform
     * @return message describing the action result
     */
    public String performLocationAction(int actionIndex) {
        return performLocationAction(actionIndex, null);
    }

    /**
     * Performs an action available at the current location with optional request payload.
     * @param actionIndex the index of the action to perform
     * @param request optional action request payload
     * @return message describing the action result
     */
    public String performLocationAction(int actionIndex, ActionRequest request) {
        Sim active = simManager.getActiveSim();
        if (active == null) return "No active sim.";
        if (!active.isAlive()) return active.getName() + " is no longer in the simulation.";
        
        String result = locationManager.performLocationAction(active, actionIndex, request);
        timeManager.checkTimeRules(simManager.getAllSims(), this);
        simManager.removeDeadSims(this);
        return flavorService.decorate(result, active, timeManager.getClock());
    }

    /**
     * Uses an object at the current location.
     * @param objectKey the key of the object to use
     * @return message describing what happened
     */
    public String useObject(String objectKey) {
        return locationManager.useObject(objectKey);
    }

    // ==================== Action Execution ====================
    
    /**
     * Performs an action on the active Sim.
     * @param action the Action to perform
     * @return message describing the action result
     */
    public String performAction(Action action) {
        return performAction(action, null);
    }

    /**
     * Performs an action on the active Sim with optional request payload.
     * @param action the Action to perform
     * @param request optional action request payload
     * @return message describing the action result
     */
    public String performAction(Action action, ActionRequest request) {
        Sim active = simManager.getActiveSim();
        String result = actionExecutor.performAction(active, action, this, request);
        timeManager.checkTimeRules(simManager.getAllSims(), this);
        
        simManager.removeDeadSims(this);
        return flavorService.decorate(result, active, timeManager.getClock());
    }

    /**
     * Changes the active Sim's job.
     * @param jobName the name of the new job
     * @return message describing the job change result
     */
    public String changeJob(String jobName) {
        return actionExecutor.changeJob(simManager.getActiveSim(), jobName);
    }

    // ==================== UI Input ====================
    
    /**
     * Sets the UI input handler.
     * @param input the Input instance for user interaction
     */
    public void setUIInput(Input input) {
        this.uiInput = input;
    }

    /**
     * Gets the UI input handler.
     * @return the Input instance
     */
    public Input getUIInput() {
        return uiInput;
    }

    // ==================== Utility ====================
    
    /**
     * Gets the game logger.
     * @return the GameLogger instance
     */
    public GameLogger getLogger() {
        return logger;
    }
    
    /**
     * Gets the Sim manager.
     * @return the SimManager instance
     */
    public SimManager getSimManager() {
        return this.simManager;
    }
}
