package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Bank Teller job: high intelligence skill, works at bank.
 */
public final class BankTellerJob implements Job {
    @Override public String name() { return "Bank Teller"; }
    @Override public double salary(int level) { return 150 + level * 30; }
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
