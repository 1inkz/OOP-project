package simscli.jobs;

import simscli.stats.SkillType;

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
