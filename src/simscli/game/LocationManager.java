package simscli.game;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import simscli.location.Bank;
import simscli.location.Home;
import simscli.location.Hospital;
import simscli.location.Location;
import simscli.location.LocationKey;
import simscli.location.Casino;
import simscli.actions.request.ActionRequest;
import simscli.policy.TravelEffectPolicy;
import simscli.policy.WalkTravelFatiguePolicy;
import simscli.location.Park;
import simscli.location.PetStore;
import simscli.location.Restaurant;
import simscli.location.Street;
import simscli.sims.Sim;
import simscli.world.Usable;
import java.util.List;

/**
 * Manages all game locations and world objects.
 * Encapsulates location and object registry and travel logic.
 */
public class LocationManager {
    private final Map<String, Location> locations;
    private final Map<String, Usable> objects;
    private final TravelEffectPolicy travelEffectPolicy;
    private Game game;

    /**
     * Creates a LocationManager and initializes location/object registries.
     * 
     * @param logger the GameLogger instance (stored implicitly for future use)
     */
    public LocationManager(GameLogger logger) {
        this.locations = new LinkedHashMap<>();
        this.objects = new LinkedHashMap<>();
        this.travelEffectPolicy = new WalkTravelFatiguePolicy();
        this.game = null; // Will be set after Game initialization
    }

    /**
     * Sets the Game instance for context-dependent operations.
     * Must be called after LocationManager is created.
     * 
     * @param game the Game instance
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Initializes all game locations and world objects.
     * Call this once during game startup to register locations and objects.
     */
    public void initialize() {
        registerLocations();
        registerWorldObjects();
    }

    /**
     * Gets an unmodifiable map of all registered locations.
     * 
     * @return immutable map of location keys to Location objects
     */
    public Map<String, Location> getLocations() {
        return Collections.unmodifiableMap(locations);
    }

    /**
     * Gets an unmodifiable map of all registered world objects.
     * 
     * @return immutable map of object keys to Usable objects
     */
    public Map<String, Usable> getObjects() {
        return Collections.unmodifiableMap(objects);
    }

    /**
     * Registers all game locations (Street, Home, Park, Bank, Restaurant, Hospital, PetStore).
     * Called internally during initialization.
     */
    private void registerLocations() {
        addLocation(new Street());
        addLocation(new Home());
        addLocation(new Park());
        addLocation(new Bank());
        addLocation(new Restaurant());
        addLocation(new Hospital());
        addLocation(new PetStore());
        addLocation(new Casino());
    }

    /**
     * Adds a location to the registry by its key.
     * 
     * @param loc the Location to register
     */
    private void addLocation(Location loc) {
        locations.put(loc.key(), loc);
    }

    /**
     * Registers all world objects (Fridge, Bed, Shower, Toilet, TV, Computer, Bookshelf, Treadmill).
     * Called internally during initialization.
     */
    private void registerWorldObjects() {
        addObject("fridge", new simscli.world.Fridge());
        addObject("bed", new simscli.world.Bed());
        addObject("shower", new simscli.world.ShowerStall());
        addObject("toilet", new simscli.world.Toilet());
        addObject("tv", new simscli.world.TV());
        addObject("computer", new simscli.world.Computer());
        addObject("bookshelf", new simscli.world.Bookshelf());
        addObject("treadmill", new simscli.world.Treadmill());
    }

    /**
     * Adds a world object to the registry.
     * 
     * @param key the identifier for the object
     * @param obj the Usable object to register
     */
    private void addObject(String key, Usable obj) {
        objects.put(key, obj);
    }

    /**
     * Handles traveling to a location.
     * Applies movement costs and executes location entry actions.
     */
    public String travelTo(Sim activeSim, String destinationKey) {
        try {
            return travelTo(activeSim, LocationKey.fromKey(destinationKey));
        } catch (IllegalArgumentException e) {
            return "Unknown location. Try: " + locations.keySet();
        }
    }

    /**
     * Handles traveling to a location.
     * Applies movement costs and executes location entry actions.
     */
    public String travelTo(Sim activeSim, LocationKey destination) {
        if (activeSim == null) return "No active sim.";

        Location dest = locations.get(destination.key());
        if (dest == null) return "Unknown location. Try: " + locations.keySet();

        if (!dest.canEnter(activeSim)) {
            return activeSim.getName() + " cannot enter " + dest.name() + ".";
        }

        Location origin = activeSim.getLocation();
        travelEffectPolicy.onTravel(activeSim, origin, dest);

        activeSim.setLocation(dest);
        return dest.onEnter(activeSim);
    }

    /**
     * Performs a location action for the active Sim.
     * 
     * Retrieves the action at the given index from the current location
     * and executes it through the Game context.
     * 
     * @param activeSim the Sim performing the action
     * @param actionIndex the index of the action in the current location's action list
     * @return a message describing the action result
     */
    public String performLocationAction(Sim activeSim, int actionIndex) {
        return performLocationAction(activeSim, actionIndex, null);
    }

    /**
     * Performs a location action for the active Sim.
     *
     * @param activeSim the Sim performing the action
     * @param actionIndex the index of the action in the current location's action list
     * @param request optional action request payload
     * @return a message describing the action result
     */
    public String performLocationAction(Sim activeSim, int actionIndex, ActionRequest request) {
        if (activeSim == null) return "No active sim.";
        if (!activeSim.isAlive()) return activeSim.getName() + " is no longer in the simulation.";
        if (game == null) return "Game context not initialized.";

        List<simscli.actions.Action> acts = activeSim.getLocation().actions(activeSim);
        if (actionIndex < 0 || actionIndex >= acts.size()) return "Invalid action index.";

        simscli.actions.Action action = acts.get(actionIndex);
        String msg = action.perform(activeSim, new GameContext(game), request);
        
        return "[" + activeSim.getLocation().name() + "] " + msg;
    }

    /**
     * Uses a world object from the registry.
     * 
     * Objects include furniture (bed, fridge, shower) that can be interacted with.
     * Executes the object's action through the Game context.
     * 
     * @param objectKey the identifier of the object to use
     * @return a message describing the object interaction result
     */
    public String useObject(String objectKey) {
        Usable u = objects.get(objectKey.toLowerCase());
        if (u == null) return "Unknown object. Try: " + objects.keySet();
        if (game == null) return "Game context not initialized.";
        
        return u.action().perform(null, new GameContext(game)).toString();
    }
}
