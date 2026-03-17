package simscli.game;

import java.util.*;
import java.util.concurrent.*;
import simscli.SaveGame;
import simscli.actions.*;
import simscli.jobs.*;
import simscli.location.*;
import simscli.sims.*;
import simscli.stats.*;
import simscli.ui.Input;
import simscli.world.*;

public final class Game {
    private final List<Sim> sims = new ArrayList<>();
    private final Map<String, Usable> objects = new LinkedHashMap<>();
    private int activeIndex = -1;
    private GameClock clock; 
    private final Map<String, Location> locations = new LinkedHashMap<>();
    private final int gameStartDay;
    private Input uiInput;  // Injected by ConsoleUI

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
        
        List<String> pendingMessages = activeSim().getAndClearPendingLoanMessages();
        if (!pendingMessages.isEmpty()) {
            System.out.println("\n\u001B[33m[Sims Reminder]\u001B[0m You have switched to " + activeSim().getName() + "，below is unread message");
            for (String msg : pendingMessages) {
                System.out.println(msg + "\n");
            }
        }
    }
    
    public void addSim(Sim sim) {
        this.sims.add(sim);
    }

    public int getActiveSimIndex() {
        return this.activeIndex;
    }
    
    // Use for loading game data to avoid repeated data
    public void clearSimList() {
    	this.sims.clear();
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
        sim.setGame(this);
        sims.add(sim);
        if (activeIndex == -1) activeIndex = 0;
        return sim;
    }
    
    private void removeDeadSims() {
    String deadActiveSimName = null;
    String deadReason = null;

    Iterator<Sim> it = sims.iterator();
    int index = 0;

    while (it.hasNext()) {
        Sim sim = it.next();

        if (!sim.isAlive()) {
            String reason = getZeroNeedReason(sim);
            System.out.println(sim.getName() + " was eliminated because " + reason + " reached 0!");

            if (index == activeIndex) {
                deadActiveSimName = sim.getName();
                deadReason = reason;
            }

            it.remove();
            continue;
        }

        index++;
    }

    if (sims.isEmpty()) {
        activeIndex = -1;
        SaveGame.saveGame(this);
        return;
    }

    if (deadActiveSimName != null) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        activeIndex = 0;

        System.out.println("\n[Eliminated] " + deadActiveSimName + " can no longer be played because "
                + deadReason + " reached 0.");
        System.out.println("Switching to " + activeSim().getName() + "...\n");

        SaveGame.saveGame(this);
    } else if (activeIndex >= sims.size()) {
        activeIndex = 0;
        SaveGame.saveGame(this);
    }
}

private String getZeroNeedReason(Sim sim) {
    if (sim.getNeeds().get(NeedType.HUNGER) <= 0) return "HUNGER";
    if (sim.getNeeds().get(NeedType.ENERGY) <= 0) return "ENERGY";
    if (sim.getNeeds().get(NeedType.HYGIENE) <= 0) return "HYGIENE";
    if (sim.getNeeds().get(NeedType.SOCIAL) <= 0) return "SOCIAL";
    if (sim.getNeeds().get(NeedType.FUN) <= 0) return "FUN";
    if (sim.getNeeds().get(NeedType.BLADDER) <= 0) return "BLADDER";
    return "an unknown need";
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
    
    public void setUIInput(Input input) {
        this.uiInput = input;
    }
    
    public Input getUIInput() {
        return uiInput;
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
                    sim.updatePetsHourly();
                }
            }
        }

        // 4. Run time-based rules
        checkTimeRules(false);
        
        // Check loan day
        checkLoanOverdueRules();

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
    boolean inCorrectLocation =
            (hasHouse && sim.getLocation().key().equals("home")) ||
            (!hasHouse && sim.getLocation().key().equals("park"));

    if (!inCorrectLocation && sim == activeSim()) {
        System.out.println("\nYou are too tired! Forced to sleep... See you next morning 8:00 AM!");
    }

    sim.getBankingSystem().settleInterest();
    sim.getNeeds().set(NeedType.ENERGY, 90);
    sim.getNeeds().set(NeedType.HUNGER, 30);
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
        addLocation(new PetStore());
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

        String[] workLocations = s.getJob().getWorkLocations();
        if (workLocations == null || workLocations.length == 0) {
            return s.getName() + " is now a " + s.getJobName() + ".";
        }

        return s.getName() + " is now a " + s.getJobName()
                + ". You can work at: " + formatLocationList(workLocations) + ".";
    } catch (IllegalArgumentException e) {
        return "Unknown job. Try: Chef / Doctor / Engineer / Influencer";
    }
}

private String formatLocationList(String[] keys) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < keys.length; i++) {
        if (i > 0) {
            sb.append(" or ");
        }
        sb.append(formatLocationName(keys[i]));
    }

    return sb.toString();
}

private String formatLocationName(String key) {
    return switch (key.toLowerCase()) {
        case "restaurant" -> "Restaurant";
        case "hospital" -> "Hospital";
        case "bank" -> "Bank";
        case "park" -> "Park";
        case "home" -> "Home";
        case "street" -> "Street";
        default -> key.substring(0, 1).toUpperCase() + key.substring(1);
    };
}



    // End: Job

    
    // Start: Action
    public String performAction(Action action) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        if (!s.isAlive()) return s.getName() + " is no longer in the simulation.";

        String msg = action.perform(s, new GameContext(this));

        removeDeadSims();
        
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


    // Start: Asset
    private void checkLoanOverdueRules() {
    	Sim activeSim = activeSim();
    	
        for (Sim sim : sims) {
            if (!sim.isAlive() || !sim.hasAssetLoan()) {
                continue; 
            }
            
            int overdueDays = sim.getLoanOverdueDays(this);
            
            if (overdueDays >= 60 && overdueDays < 80) {
            	     
                if (sim == activeSim) {
                	System.out.println(sim.repossessAsset());
                } else {
                    sim.addPendingLoanMessage(sim.repossessAsset());
                }
            }
            
            if (overdueDays >= 80 && sim.isInsolvent()) {
            	sim.setAlive(false);
                System.out.println("\u001B[31m[Insolvent]\u001B[0m " + sim.getName() + " die from bankrupt");
                sim.clearPendingLoanMessages();
            }
        }

        removeDeadSims();
    }
    // End: Asset

   public void cleanupDeadSims() {
    removeDeadSims();
}









    
    
}