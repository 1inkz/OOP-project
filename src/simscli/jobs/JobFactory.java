package simscli.jobs;

/**
 * Factory for creating Job instances.
 * Encapsulates all job creation logic in one place.
 */
public final class JobFactory {
    private JobFactory() {}

    /**
     * Creates a job instance by name.
     * @param name the job name (case-insensitive)
     * @return the corresponding Job implementation
     * @throws IllegalArgumentException if job name is unknown
     */
    public static Job create(String name) {
        if (name == null) throw new IllegalArgumentException("job required");

        switch (name.trim().toLowerCase()) {
            case "jobless":
            case "unemployed":
                return new JoblessJob();

            case "chef": return new ChefJob();
            case "doctor": return new DoctorJob();
            case "bank teller": return new BankTellerJob();
            case "influencer": return new InfluencerJob();

            default:
                throw new IllegalArgumentException("Unknown job: " + name);
        }
    }
}