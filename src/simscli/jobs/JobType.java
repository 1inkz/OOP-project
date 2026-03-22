package simscli.jobs;

/**
 * Typed job identifiers with display names.
 */
public enum JobType {
    JOBLESS("Jobless"),
    CHEF("Chef"),
    DOCTOR("Doctor"),
    BANK_TELLER("Bank Teller"),
    INFLUENCER("Influencer");

    private final String displayName;

    JobType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static JobType fromName(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name required");
        }

        String normalized = raw.trim().toLowerCase();
        switch (normalized) {
            case "jobless":
            case "unemployed":
                return JOBLESS;
            case "chef":
                return CHEF;
            case "doctor":
                return DOCTOR;
            case "bank teller":
                return BANK_TELLER;
            case "influencer":
                return INFLUENCER;
            default:
                throw new IllegalArgumentException("Unknown job: " + raw);
        }
    }
}
