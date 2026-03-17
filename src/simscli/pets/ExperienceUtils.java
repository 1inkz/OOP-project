package simscli.pets;

public final class ExperienceUtils {
    private ExperienceUtils() {}

    /**
     * Calculate level based on total experience.
     * @param totalExp Total experience points
     * @param expPerLevel Experience required per level
     * @return Current level (minimum 1)
     */
    public static int calculateLevel(int totalExp, int expPerLevel) {
        return Math.max(1, (totalExp / expPerLevel) + 1);
    }
}
