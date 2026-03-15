package simscli.jobs;

import simscli.stats.SkillType;

public final class EngineerJob implements Job {
    @Override public String name() { return "Engineer"; }
    @Override public double salary(int level) { return 110 + level * 30; }
    @Override public boolean canWork() { return true; }
    @Override public String[] getWorkLocations() { return new String[] { "bank" };}


    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.INTELLIGENCE,
            SkillType.CREATIVITY,
            SkillType.WORK_ETHIC
        };
    }
}
