package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Doctor job implementation: requires intelligence and charisma, works at hospital.
 */
public final class DoctorJob implements Job {
    @Override public String name() { return "Doctor"; }
    @Override public double salary(int level) { return 120 + level * 35; }
    @Override public boolean canWork() { return true; }
    @Override public String[] getWorkLocations() {return new String[] { "hospital" };}


    @Override
    public SkillType[] primarySkills() {
        return new SkillType[] {
            SkillType.INTELLIGENCE,
            SkillType.CHARISMA,
            SkillType.WORK_ETHIC
        };
    }
}
