package simscli.jobs;

public final class JobFactory {
    private JobFactory() {}

    public static Job create(String name) {
        if (name == null) throw new IllegalArgumentException("job required");

        switch (name.trim().toLowerCase()) {
            case "jobless":
            case "unemployed":
                return new JoblessJob();

            case "chef": return new ChefJob();
            case "doctor": return new DoctorJob();
            case "engineer": return new EngineerJob();
            case "influencer": return new InfluencerJob();

            default:
                throw new IllegalArgumentException("Unknown job: " + name);
        }
    }
}