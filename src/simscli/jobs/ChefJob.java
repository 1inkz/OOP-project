package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Chef job implementation: high cooking skill, works at restaurant.
 */
public final class ChefJob implements Job {
    @Override public String name() { return "Chef"; }
    @Override public double salary(int level) { return 90 + level * 22; }
    @Override public boolean canWork() { return true; }
    @Override public String[] getWorkLocations() {
    return new String[] { "restaurant" };
}


    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.COOKING,
            SkillType.CREATIVITY,
            SkillType.WORK_ETHIC
        };
    }
}
