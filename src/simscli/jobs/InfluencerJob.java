package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Influencer job: requires charisma, can work at park or restaurant.
 */
public final class InfluencerJob implements Job {
    @Override public String name() { return "Influencer"; }
    @Override public double salary(int level) { return 70 + level * 18; }
    @Override public boolean canWork() { return true; }

    @Override
    public String[] getWorkLocations() {
        return new String[] { "park", "restaurant" };
    }

    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.CHARISMA,
            SkillType.CREATIVITY,
            SkillType.GAMING,
            SkillType.WORK_ETHIC
        };
    }
}
