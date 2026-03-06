package simscli.game;

import simscli.actions.*;
import simscli.jobs.JobFactory;
import simscli.sims.*;
import simscli.stats.NeedType;
import simscli.world.*;

import java.util.*;
import java.util.concurrent.*;

import simscli.location.*;

public final class Game {
    private final List<Sim> sims = new ArrayList<>();
    private final Map<String, Usable> objects = new LinkedHashMap<>();

    private int day = 1;
    private int hour = 8;
    private int activeIndex = -1;

    private final java.util.Map<String, Location> locations = new java.util.LinkedHashMap<>();

    // Multithreading: used only to update NPC sims in parallel (not forced, but clean).
    private final ExecutorService npcPool = Executors.newFixedThreadPool(
            Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors()))
    );

    public Game() {
        registerWorldObjects();
        registerLocations();
    }

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

    public List<Sim> sims() {
        return Collections.unmodifiableList(sims);
    }

    public Map<String, Location> location() {
        return Collections.unmodifiableMap(locations);
    }

    public String timeString() {
        return String.format("Day %d, %02d:00", day, hour);
    }

    public Sim activeSim() {
        if (activeIndex < 0 || activeIndex >= sims.size()) return null;
        return sims.get(activeIndex);
    }

    public void setActiveSim(int index) {
        if (index < 0 || index >= sims.size()) throw new IllegalArgumentException("bad index");
        activeIndex = index;
    }

    public Sim createSim(String name, SimType type) {
        Sim sim;
        switch (type) {
            case CHILD: sim = new ChildSim(name); break;
            case ADULT: sim = new AdultSim(name); break;
            case ELDER: sim = new ElderSim(name); break;
            default: throw new IllegalArgumentException("unknown type");
        }

        sim.setLocation(locations.get("home"));
        sim.setJob(JobFactory.create("jobless"));
        sims.add(sim);
        if (activeIndex == -1) activeIndex = 0;
        return sim;
    }

    private void registerLocations() 
    {
        addLocation(new Home());
        addLocation(new Park());
        addLocation(new Bank());
        addLocation(new Restaurant());
    }

    private void addLocation(Location loc) 
    {
    locations.put(loc.key(), loc);
    }

    public java.util.Map<String, Location> locations() 
    {
        return java.util.Collections.unmodifiableMap(locations);
    }

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

    public String performAction(Action action) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        if (!s.isAlive()) return s.getName() + " is no longer in the simulation.";

        String msg = action.perform(s, new GameContext(this));
        tickOneHour(); // every action consumes time
        return msg;
    }

    public String useObject(String key) {
        Usable u = objects.get(key.toLowerCase());
        if (u == null) return "Unknown object. Try: " + objects.keySet();
        return performAction(u.action());
    }

    public String travelTo(String key) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        Location dest = locations.get(key.toLowerCase());
        if (dest == null) return "Unknown location. Try: " + locations.keySet();

        if (!dest.canEnter(s)) return s.getName() + " cannot enter " + dest.name() + ".";
        s.setLocation(dest);

        // travel costs time
        tickOneHour();
        return dest.onEnter(s);
    }

    public String performLocationAction(int actionIndex) {
        Sim s = activeSim();
        if (s == null) return "No active sim.";
        if (s.getLocation() == null) return "Sim has no location.";

        java.util.List<Action> acts = s.getLocation().actions();
        if (actionIndex < 0 || actionIndex >= acts.size()) return "Invalid action index.";

        String msg = performAction(acts.get(actionIndex));
        return "[" + s.getLocation().name() + "] " + msg;
    }   

    public void tickOneHour() {
        hour++;
        if (hour >= 24) {
            hour = 0;
            day++;
        }

        // 1) active sim ticks on main thread
        GameContext ctx = new GameContext(this);
        Sim active = activeSim();
        if (active != null) active.tickHour(ctx);

        // 2) NPC sims tick in parallel (safe: each sim mutates only itself)
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < sims.size(); i++) {
            if (i == activeIndex) continue;
            Sim sim = sims.get(i);
            tasks.add(() -> {
                sim.tickHour(ctx);
                autoHelpIfCritical(sim); // NPC autonomy (simple)
                return null;
            });
        }

        try {
            npcPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        removeDeadSims(); // allow GC to reclaim removed sims
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
            ActionFactory.create(ActionType.BRUSH_TEETH).perform(sim, new GameContext(this));
        }
    }

    private void removeDeadSims() {
        for (int i = sims.size() - 1; i >= 0; i--) {
            if (!sims.get(i).isAlive()) {
                sims.remove(i); // removing references => GC can collect
                if (activeIndex >= sims.size()) activeIndex = sims.size() - 1;
            }
        }
        if (sims.isEmpty()) activeIndex = -1;
    }

    /** Must be called on quit to avoid thread leak (GC best practice). */
    public void shutdown() {
        npcPool.shutdownNow();
    }
}