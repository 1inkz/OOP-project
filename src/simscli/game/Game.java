package simscli.game;

import simscli.actions.*;
import simscli.jobs.*;
import simscli.sims.*;
import simscli.stats.*;
import simscli.world.*;
import simscli.location.*;
import simscli.asset.*;
import simscli.*;

import java.util.*;
import java.util.concurrent.*;

public final class Game {
    private final List<Sim> sims = new ArrayList<>();
    private final Map<String, Usable> objects = new LinkedHashMap<>();
    private int activeIndex = -1;
    private GameClock clock; 
    private final Map<String, Location> locations = new LinkedHashMap<>();
    private final int gameStartDay;

    // Multithreading: used only to update NPC sims in parallel (not forced, but clean).
    private final ExecutorService npcPool = Executors.newFixedThreadPool(
            Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()))
    );
    
    // Start: Game
    public Game() {
    	clock = new GameClock(1, 480);
    	this.gameStartDay = clock.getDayNumber();
        registerWorldObjects();
        registerLocations();
    }
    
    public void resetGame() {
        this.sims.clear();
        this.activeIndex = -1;
        clock.resetNewGame();
    }

    /** Must be called on quit to avoid thread leak (GC best practice). */
    public void shutdown() {
        npcPool.shutdownNow();
    }
    // End: Game
    
    
    // Start: Sims
    public List<Sim> sims() {
        return Collections.unmodifiableList(sims);
    }
    
    public Sim activeSim() {
        if (activeIndex < 0 || activeIndex >= sims.size()) return null;
        return sims.get(activeIndex);
    }

    public void setActiveSim(int index) {
        if (index < 0 || index >= sims.size()) throw new IllegalArgumentException("bad index");
        activeIndex = index;
    }
    
    public void addSim(Sim sim) {
        this.sims.add(sim);
    }

    public int getActiveSimIndex() {
        return this.activeIndex;
    }
    
    public Sim createSim(String name, SimType type) {
        Sim sim;
        switch (type) {
            case CHILD: sim = new ChildSim(name, this); break;
            case ADULT: sim = new AdultSim(name, this); break;
            case ELDER: sim = new ElderSim(name, this); break;
            default: throw new IllegalArgumentException("unknown type");
        }

        sim.setLocation(locations.get("street"));
        sim.setJob(JobFactory.create("jobless"));
        sims.add(sim);
        if (activeIndex == -1) activeIndex = 0;
        return sim;
    }
    
    private void removeDeadSims() {

        boolean activeSimDied = false;

        Iterator<Sim> it = sims.iterator();
        int index = 0;

        while (it.hasNext()) {
            Sim sim = it.next();

            if (!sim.isAlive()) {
                System.out.println(sim.getName() + " was eliminated (needs hit 0)!");

                if (index == activeIndex) {
                    activeSimDied = true;
                }

                it.remove();
            }

            index++;
        }

        if (activeSimDied) {
            activeIndex = -1; // force player to pick another sim
        }
    }
    // End: Sim
    
    
    // Start: Time
   public String timeString() {
        return clock.getFormattedTime();
    }
    
    public GameClock getClock() {
    	return clock;
    }
    
    public void setClock(GameClock clock) {
        this.clock = clock;
    }
    
    public int getGameStartDay() {
        return gameStartDay;
    }
    
    private void advanceGameTime(int minutes) {

        // 1. Advance the clock
        clock.spendMinutes(minutes);

        // 2. Convert minutes → hours for decay
        int hours = minutes / 60;

        // 3. Apply decay to all sims
        for (Sim sim : sims) {
            if (sim.isAlive()) {
                for (int i = 0; i < hours; i++) {
                    sim.applyEffect(sim.hourlyDecay());
                }
            }
        }

        // 4. Run time-based rules
        checkTimeRules(false);

        // 5. Remove dead sims
        removeDeadSims();
    }
    
    public void advanceTimeForAction() {
        advanceGameTime(60);

        // NPC behaviour remains the same
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < sims.size(); i++) {
            if (i == activeIndex) continue;

            Sim sim = sims.get(i);

            tasks.add(() -> {
                autoHelpIfCritical(sim);
                return null;
            });
        }

        try {
            npcPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Real-time auto-advance
    public void autoAdvanceRealTime(double deltaSeconds) {

        clock.advanceByRealTime(deltaSeconds);

        int hoursPassed = clock.getHoursPassedFromAccumulator();

        if (hoursPassed > 0) {
            advanceGameTime(hoursPassed * 60);
        }
    }

    public void checkTimeRules(boolean action) {
        int currentHour = clock.getHour(); 
       
        if (currentHour == 20 && !action) {
            System.out.println("\n[GAME] It's 8pm — all sims should head to bed!");
        }
		else if (currentHour == 21 && !action) {
            System.out.println("\n[GAME] It's 9pm — sleep now!");
        }

        if (currentHour == 22 || action) {
        	clock.resetToNextDayMorning(); 
        	for (Sim sim : sims) {
                if (!sim.isAlive()) continue;
                
                boolean hasHouse = sim.getOwnedHouse() != null;
                boolean inCorrectLocation = (hasHouse && sim.getLocation().key().equals("home"))
                        || (!hasHouse && sim.getLocation().key().equals("park"));
                if (!inCorrectLocation) {
                    System.out.println("\nYou are too tired! Forced to sleep... See you next morning 8:00 AM!");
                }     
                
                if (sim.isAlive()) {
                	sim.getBankingSystem().settleInterest();
                	sim.getNeeds().set(NeedType.ENERGY, 90); 
                	sim.getNeeds().set(NeedType.HUNGER, 30);
                }
        	}
        }
    }
    // End: Time


    // Start: Location
    public Map<String, Location> location() {
        return Collections.unmodifiableMap(locations);
    }
    
    private void registerLocations() {
    	addLocation(new Street()); 
    	addLocation(new Home());
        addLocation(new Park());
        addLocation(new Bank());
        addLocation(new Restaurant());
        addLocation(new Hospital());
    }

    private void addLocation(Location loc) {
    	locations.put(loc.key(), loc);
    }
    
    public String travelTo(String key) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";    
        
        Location dest = locations.get(key.toLowerCase());
        if (dest == null) return "Unknown location. Try: " + locations.keySet();

        if (!dest.canEnter(s)) return s.getName() + " cannot enter " + dest.name() + ".";
        
        // Walking tired
        if (s.getOwnedCar() == null) {
            s.applyEffect(Effect.none()
                    .plus(NeedType.HUNGER, -10)
                    .plus(NeedType.ENERGY, -10));
        }
        
        s.setLocation(dest);

        // travel costs time
        return dest.onEnter(s);
    }

    public String performLocationAction(int actionIndex) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";

        java.util.List<Action> acts = s.getLocation().actions(s);
        if (actionIndex < 0 || actionIndex >= acts.size()) return "Invalid action index.";

        String msg = performAction(acts.get(actionIndex));
        return "[" + s.getLocation().name() + "] " + msg;
    }   
    // End: Location
    
    
    // Start: World Object
    private void registerWorldObjects() {
        addObject(new Fridge());
        addObject(new Bed());
        addObject(new ShowerStall());
        addObject(new Toilet());
        addObject(new TV());
        addObject(new Computer());
        addObject(new Bookshelf());
        addObject(new Treadmill());
    }

    private void addObject(Usable u) {
        objects.put(u.key(), u);
    }
    
    public String useObject(String key) {
        Usable u = objects.get(key.toLowerCase());
        if (u == null) return "Unknown object. Try: " + objects.keySet();
        return performAction(u.action());
    }
    // End: World Object


    // Start: Job
    public String changeJob(String jobName) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        try {
            s.setJob(JobFactory.create(jobName));
            return s.getName() + " is now a " + s.getJobName() + ".";
        } catch (IllegalArgumentException e) {
            return "Unknown job. Try: Chef / Doctor / Engineer / Influencer";
        }
    }
    // End: Job

    
    // Start: Action
    public String performAction(Action action) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        if (!s.isAlive()) return s.getName() + " is no longer in the simulation.";

        String msg = action.perform(s, new GameContext(this));
        return msg;
    }
    
    private void autoHelpIfCritical(Sim sim) {
        if (!sim.isAlive()) return;

        // NPC auto behaviour: if critical, take a sensible action.
        if (sim.isCritical(NeedType.HUNGER)) {
            ActionFactory.create(ActionType.EAT_SNACK).perform(sim, new GameContext(this));
        } else if (sim.isCritical(NeedType.ENERGY)) {
            ActionFactory.create(ActionType.NAP).perform(sim, new GameContext(this));
        } else if (sim.isCritical(NeedType.BLADDER)) {
            ActionFactory.create(ActionType.USE_TOILET).perform(sim, new GameContext(this));
        } else if (sim.isCritical(NeedType.HYGIENE)) {
            if (sim.getOwnedHouse() != null) {
            	ActionFactory.create(ActionType.BRUSH_TEETH).perform(sim, new GameContext(this)); 
            } else {
            	ActionFactory.create(ActionType.CLEAN_PUBLIC).perform(sim, new GameContext(this)); 
            }
        }
    }
    // End: Action












    
    
}