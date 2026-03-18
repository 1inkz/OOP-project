package simscli.stats;

/**
 * Enum of all skill types with display names.
 */
public enum SkillType {
    COOKING("Cooking"),
    CLEANING("Cleaning"),
    CHARISMA("Charisma"),
    FITNESS("Fitness"),
    INTELLIGENCE("Intelligence"),
    CREATIVITY("Creativity"),
    GAMING("Gaming"),
    WORK_ETHIC("Work Ethic");

    private final String displayName;

    /**
     * Creates a skill type with a display name.
     * @param displayName the human-readable skill name
     */
    SkillType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this skill.
     * @return human-readable name
     */
    public String displayName() {
        return displayName;
    }
}
