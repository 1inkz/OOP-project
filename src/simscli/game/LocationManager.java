package simscli.game;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import simscli.location.Bank;
import simscli.location.Home;
import simscli.location.Hospital;
import simscli.location.Location;
import simscli.location.Park;
import simscli.location.PetStore;
import simscli.location.Restaurant;
import simscli.location.Street;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.world.Usable;
import java.util.List;

/**
 * Manages all game locations and world objects.
 * Encapsulates location and object registry and travel logic.
 */
public class LocationManager {
    private final Map<String, Location> locations;
    private final Map<String, Usable> objects;
    private final GameLogger logger;

    public LocationManager(GameLogger logger) {
        this.locations = new LinkedHashMap<>();
        this.objects = new LinkedHashMap<>();
        this.logger = logger;
    }

    public void initialize() {
        registerLocations();
        registerWorldObjects();
    }

    public Map<String, Location> getLocations() {
        return Collections.unmodifiableMap(locations);
    }

    public Map<String, Usable> getObjects() {
        return Collections.unmodifiableMap(objects);
    }

    private void registerLocations() {
        addLocation(new Street());
        addLocation(new Home());
        addLocation(new Park());
        addLocation(new Bank());
        addLocation(new Restaurant());
        addLocation(new Hospital());
        addLocation(new PetStore());
    }

    private void addLocation(Location loc) {
        locations.put(loc.key(), loc);
    }

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

    private void addObject(String key, Usable obj) {
        objects.put(key, obj);
    }

    /**
     * Handles traveling to a location.
     * Applies movement costs and executes location entry actions.
     */
    public String travelTo(Sim activeSim, String destinationKey) {
        if (activeSim == null) return "No active sim.";

        Location dest = locations.get(destinationKey.toLowerCase());
        if (dest == null) return "Unknown location. Try: " + locations.keySet();

        if (!dest.canEnter(activeSim)) {
            return activeSim.getName() + " cannot enter " + dest.name() + ".";
        }

        // Walking tired
        if (activeSim.getOwnedCar() == null) {
            activeSim.applyEffect(Effect.none()
                    .plus(NeedType.HUNGER, -10)
                    .plus(NeedType.ENERGY, -10));
        }

        activeSim.setLocation(dest);
        return dest.onEnter(activeSim);
    }

    /**
     * Executes a location action.
     */
    public String performLocationAction(Sim activeSim, int actionIndex) {
        if (activeSim == null) return "No active sim.";

        List<simscli.actions.Action> acts = activeSim.getLocation().actions(activeSim);
        if (actionIndex < 0 || actionIndex >= acts.size()) return "Invalid action index.";

        simscli.actions.Action action = acts.get(actionIndex);
        String msg = action.perform(activeSim, new GameContext(new Game()));
        
        return "[" + activeSim.getLocation().name() + "] " + msg;
    }

    /**
     * Uses a world object.
     */
    public String useObject(String objectKey) {
        Usable u = objects.get(objectKey.toLowerCase());
        if (u == null) return "Unknown object. Try: " + objects.keySet();
        
        return u.action().perform(null, new GameContext(new Game())).toString();
    }
}
