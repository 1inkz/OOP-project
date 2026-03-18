package simscli.jobs;

import simscli.stats.SkillType;

/**
 * Interface for all job types in the game.
 * Defines salary, work location, and skill requirements.
 */
public interface Job {
    /**
     * Gets the display name of this job.
     * @return job name
     */
    String name();
    /**
     * Calculates salary based on job level.
     * @param level the Sim's current job level
     * @return salary amount for this level
     */
    double salary(int level);
    /**
     * Checks if this job allows work actions.
     * @return true if Sims can work this job
     */
    boolean canWork();
    /**
     * Gets locations where this job can be performed.
     * @return array of location keys
     */
    String[] getWorkLocations();

    /**
     * Gets the primary skills for this job.
     * Used to calculate salary multiplier from skill levels.
     * @return array of relevant skill types (default: WORK_ETHIC)
     */
    default SkillType[] primarySkills() {
        return new SkillType[] { SkillType.WORK_ETHIC };
    }
}
