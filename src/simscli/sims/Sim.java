package simscli.sims;

import simscli.game.GameContext;
import simscli.jobs.Job;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.location.Location;
import java.util.Map;

public abstract class Sim {
    private final String name;
    private final SimType type;
    private final Needs needs = new Needs();
    
    private boolean alive = true;
    private int money = 50;
    private int jobLevel = 1;
    private Job job = simscli.jobs.JobFactory.create("jobless");
    private Location location;

    protected Sim(String name, SimType type) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name required");
        this.name = name.trim();
        this.type = type;
    }

    public final String getName() { return name; }
    public final SimType getType() { return type; }
    public final boolean isAlive() { return alive; }
    public final int getMoney() { return money; }
    public final int getJobLevel() { return jobLevel; }

    public final String getJobName() {
        return job.name();
    }

    public final void setJob(Job job) {
        this.job = job;
        this.jobLevel = 1;
    }

    /** Controlled manipulation: actions must go through this method. */
    public final void applyEffect(Effect effect) {
        if (!alive) return;
        for (Map.Entry<NeedType, Integer> e : effect.deltas().entrySet()) {
            needs.add(e.getKey(), e.getValue());
        }
        checkDeathConditions();
    }

    /** Called by Game loop. */
    public final void tickHour(GameContext ctx) {
        if (!alive) return;

        // needs decay differs per sim type (polymorphism)
        Effect decay = hourlyDecay();
        applyEffect(decay);

        // small passive money drain? (optional) - keep simple: none
        // NPC autonomy handled by Game, not by Sim (single responsibility)
    }

    public final String summaryLine() {
        return String.format(
                "%s (%s) | $%d | Job=%s L%d | H=%d E=%d Hy=%d S=%d F=%d B=%d",
                name, type, money, getJobName(), jobLevel,
                needs.get(NeedType.HUNGER),
                needs.get(NeedType.ENERGY),
                needs.get(NeedType.HYGIENE),
                needs.get(NeedType.SOCIAL),
                needs.get(NeedType.FUN),
                needs.get(NeedType.BLADDER)
        );
    }

    public final int moodScore() {
        // Simple average to show “mood”
        int sum = 0;
        for (NeedType t : NeedType.values()) sum += needs.get(t);
        return sum / NeedType.values().length;
    }

    public final boolean isCritical(NeedType t) {
        return needs.isCritical(t);
    }

    public final void earnMoney(int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount >= 0");
        money += amount;
    }

    public final String work() 
    {
        if (!alive) return name + " is not available.";

        if (!job.canWork()) 
        {
            return name + " is jobless. Get a job first!";
        }

        // working drains some needs
    Effect cost = Effect.none()
            .plus(NeedType.ENERGY, -20)
            .plus(NeedType.HUNGER, -15);
    applyEffect(cost);

    int earned = (int) Math.round(job.salary(jobLevel));
    money += earned;
    jobLevel++;

        return name + " worked as " + job.name() + " and earned $" + earned + ".";
    }

    public final Location getLocation() 
    {
    return location;
    }

    public final void setLocation(Location location) 
    {
    if (location == null) throw new IllegalArgumentException("location required");
    this.location = location;
    }

    public final void spendMoney(int amount) 
    {
    if (amount < 0) throw new IllegalArgumentException("amount >= 0");
    if (money < amount) throw new IllegalStateException("insufficient funds");
    money -= amount;
    }

    private void checkDeathConditions() {
        // If any need hits 0, sim “dies/leaves” (simple rule)
        for (NeedType t : NeedType.values()) {
            if (needs.isZero(t)) {
                alive = false;
                return;
            }
        }
    }

    /** Each subtype defines its decay pace. */
    protected abstract Effect hourlyDecay();
}