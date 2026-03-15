package simscli.stats;

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

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
