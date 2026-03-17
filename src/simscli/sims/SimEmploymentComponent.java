package simscli.sims;

import java.util.HashMap;
import java.util.Map;
import simscli.jobs.Job;
import simscli.jobs.JoblessJob;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Manages Sim employment: jobs, job levels, work actions.
 * Encapsulates job-related logic and state.
 */
public class SimEmploymentComponent {
    private Job job;
    private final Map<String, Integer> jobLevels;

    public SimEmploymentComponent() {
        this.job = new JoblessJob();
        this.jobLevels = new HashMap<>();
        jobLevels.put("Jobless", 1);
    }

    public String getJobName() {
        return job.name();
    }

    public Job getJob() {
        return job != null ? job : new JoblessJob();
    }

    public void setJob(Job newJob) {
        if (newJob == null) {
            this.job = new JoblessJob();
        } else {
            this.job = newJob;
            String jobName = newJob.name();
            if (!jobLevels.containsKey(jobName)) {
                jobLevels.put(jobName, 1);
            }
        }
    }

    public int getJobLevel() {
        String currentJobName = job.name();
        return jobLevels.getOrDefault(currentJobName, 1);
    }

    public void setJobLevel(int jobLevel) {
        if (jobLevel >= 1) {
            String currentJobName = job.name();
            jobLevels.put(currentJobName, jobLevel);
        }
    }

    public Map<String, Integer> getAllJobLevels() {
        return new HashMap<>(jobLevels);
    }

    public void setAllJobLevels(Map<String, Integer> jobLevels) {
        if (jobLevels != null) {
            this.jobLevels.clear();
            this.jobLevels.putAll(jobLevels);
        }
    }

    /**
     * Simulates work, applying costs and earning simcoin.
     * Returns work result message.
     */
    public WorkResult work(String simName) {
        if (!job.canWork()) {
            return new WorkResult(false, simName + " is jobless. Get a job first!");
        }

        Effect cost = Effect.none()
                .plus(NeedType.ENERGY, -20)
                .plus(NeedType.HUNGER, -15);

        int earned = (int) Math.round(job.salary(getJobLevel()));
        setJobLevel(getJobLevel() + 1);

        String message = simName + " worked as " + job.name() + " and earned $" + earned + ".";
        return new WorkResult(true, message, cost, earned);
    }

    /**
     * Result of a work action: whether successful, message, stats effect, and earnings.
     */
    public static class WorkResult {
        private final boolean success;
        private final String message;
        private final Effect effect;
        private final int earnings;

        public WorkResult(boolean success, String message) {
            this(success, message, Effect.none(), 0);
        }

        public WorkResult(boolean success, String message, Effect effect, int earnings) {
            this.success = success;
            this.message = message;
            this.effect = effect;
            this.earnings = earnings;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Effect getEffect() { return effect; }
        public int getEarnings() { return earnings; }
    }
}
