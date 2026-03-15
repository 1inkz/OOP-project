package simscli.game;

import java.util.*;
import java.util.concurrent.*;
import simscli.*;
import simscli.actions.*;
import simscli.jobs.*;
import simscli.location.*;
import simscli.sims.*;
import simscli.stats.Effect;
import simscli.world.*;

public final class Game {
    private final List<Sim> sims = new ArrayList<>();
    private final Map<String, Usable> objects = new LinkedHashMap<>();
    private int activeIndex = -1;
    private GameClock clock;
    private final Map<String, Location> locations = new LinkedHashMap<>();
    private final int gameStartDay;

    private final SimLifecycleManager lifecycleManager = new SimLifecycleManager();
    private final GameTimeManager timeManager = new GameTimeManager();
    private final NpcBehaviourManager npcBehaviourManager = new NpcBehaviourManager();

    // Multithreading: used only to update NPC sims in parallel
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

    /** Must be called on quit to avoid thread leak. */
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
            System.out.println("\n\u001B[33m[Sims Reminder]\u001B[0m You have switched to "
                    + activeSim().getName() + "，below is unread message");
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
        activeIndex = lifecycleManager.removeDeadSims(sims, activeIndex, this);
    }
    // End: Sims


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
        timeManager.advanceGameTime(this, sims, minutes);
        checkLoanOverdueRules();
        removeDeadSims();
    }

    public void advanceTimeForAction() {
        advanceGameTime(60);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < sims.size(); i++) {
            if (i == activeIndex) continue;

            Sim sim = sims.get(i);
            tasks.add(() -> {
                npcBehaviourManager.autoHelpIfCritical(sim, this);
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
        timeManager.applyDailyRules(this, sims, action);
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
                    .plus(simscli.stats.NeedType.HUNGER, -10)
                    .plus(simscli.stats.NeedType.ENERGY, -10));
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