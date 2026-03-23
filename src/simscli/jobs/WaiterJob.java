package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Waiter job implementation: gain cleaning skill and became fit, works at restaurant.
 */
public final class WaiterJob implements Job {
    @Override public String name() { return "Waiter"; }
    @Override public double salary(int level) { return 70 + level * 22; }
    @Override public boolean canWork() { return true; }
    @Override public String[] getWorkLocations() {
    return new String[] { "restaurant" };
}


    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.FITNESS,
            SkillType.CLEANING,
            SkillType.WORK_ETHIC
        };
    }
}
