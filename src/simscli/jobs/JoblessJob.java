package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Placeholder job for unemployed Sims.
 * Provides no salary and no work actions.
 */
public final class JoblessJob implements Job {
    @Override public String name() { return "Jobless"; }
    @Override public double salary(int level) { return 0; }
    @Override public boolean canWork() { return false; }
    @Override public String[] getWorkLocations() {return new String[0];}


    @Override
    public SkillType[] primarySkills() {
        return new SkillType[0];
    }
}
