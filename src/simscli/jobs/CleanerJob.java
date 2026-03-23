package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Cleaner job: can work at park or restaurant.
 */
public final class CleanerJob implements Job {
    @Override public String name() { return "Cleaner"; }
    @Override public double salary(int level) { return 50 + level * 18; }
    @Override public boolean canWork() { return true; }

    @Override
    public String[] getWorkLocations() {
        return new String[] { "park", "restaurant" };
    }

    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.CLEANING,
            SkillType.WORK_ETHIC
        };
    }
}
