package simscli.jobs;

/**
 * Factory for creating Job instances.
 * Encapsulates all job creation logic in one place.
 */
public final class JobFactory {
    private JobFactory() {}

    /**
     * Creates a job instance by typed identifier.
     * @param type the job type
     * @return the corresponding Job implementation
     */
    public static Job create(JobType type) {
        if (type == null) throw new IllegalArgumentException("job type required");

        switch (type) {
            case JOBLESS:
                return new JoblessJob();
            case CHEF:
                return new ChefJob();
            case DOCTOR:
                return new DoctorJob();
            case WAITER:
                return new WaiterJob();
            case CLEANER:
                return new CleanerJob();
            default:
                throw new IllegalArgumentException("Unknown job type: " + type);
        }
    }

    /**
     * Creates a job instance by name.
     * @param name the job name (case-insensitive)
     * @return the corresponding Job implementation
     * @throws IllegalArgumentException if job name is unknown
     */
    public static Job create(String name) {
        return create(JobType.fromName(name));
    }
}