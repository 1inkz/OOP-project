package simscli.jobs;

import simscli.stats.SkillType;

public interface Job {
    String name();
    double salary(int level);
    boolean canWork();
    String[] getWorkLocations();

    default SkillType[] primarySkills() {
        return new SkillType[] { SkillType.WORK_ETHIC };
    }
}
