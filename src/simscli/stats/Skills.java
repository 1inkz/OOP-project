package simscli.stats;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages all Sim skills: cooking, cleaning, charisma, fitness, intelligence, creativity, gaming, and work ethic.
 * Each skill ranges from 0 to 100 and can be gained through actions.
 */
public final class Skills {
    private static final int MIN_SKILL = 0;
    private static final int MAX_SKILL = 100;

    private final EnumMap<SkillType, BoundedStat> skills = new EnumMap<>(SkillType.class);

    public Skills() {
        for (SkillType type : SkillType.values()) {
            skills.put(type, new BoundedStat(0));
        }
    }

    /**
     * Gets the current level of a specific skill.
     * @param type the skill type to query
     * @return skill level (0-100)
     * @throws IllegalArgumentException if type is null
     */
    public int get(SkillType type) {
        validateType(type);
        return skills.get(type).get();
    }

    /**
     * Sets a skill to a specific level, clamped to 0-100.
     * @param type the skill type to set
     * @param value the new skill level
     * @throws IllegalArgumentException if type is null
     */
    public void set(SkillType type, int value) {
        validateType(type);
        skills.get(type).set(clamp(value));
    }

    /**
     * Increases a skill by the given amount.
     * @param type the skill type to improve
     * @param amount skill points to gain (must be positive)
     * @return actual points gained after clamping
     * @throws IllegalArgumentException if type is null
     */
    public int gain(SkillType type, int amount) {
        validateType(type);
        if (amount <= 0) {
            return 0;
        }

        int before = get(type);
        skills.get(type).add(amount);
        int after = get(type);
        return after - before;
    }

    /**
     * Checks if a skill is at maximum level.
     * @param type the skill type to check
     * @return true if skill value >= 100
     */
    public boolean isMaxed(SkillType type) {
        return get(type) >= MAX_SKILL;
    }

    /**
     * Calculates the average level of multiple skills.
     * @param types the skill types to average
     * @return average skill level rounded down
     */
    public int average(SkillType... types) {
        if (types == null || types.length == 0) {
            return 0;
        }

        int sum = 0;
        int count = 0;

        for (SkillType type : types) {
            if (type != null) {
                sum += get(type);
                count++;
            }
        }

        return count == 0 ? 0 : sum / count;
    }

    /**
     * Returns a defensive copy of all skill levels.
     * @return immutable snapshot of skills state
     */
    public Map<SkillType, Integer> snapshot() {
        EnumMap<SkillType, Integer> copy = new EnumMap<>(SkillType.class);
        for (Map.Entry<SkillType, BoundedStat> entry : skills.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().get());
        }
        return copy;
    }

    /**
     * Loads skill levels from a saved state.
     * @param savedValues map of skill types to levels from save file
     */
    public void loadFrom(Map<SkillType, Integer> savedValues) {
        if (savedValues == null) {
            return;
        }

        for (SkillType type : SkillType.values()) {
            int value = savedValues.getOrDefault(type, 0);
            set(type, value);
        }
    }

    private void validateType(SkillType type) {
        if (type == null) {
            throw new IllegalArgumentException("skill type required");
        }
    }

    private int clamp(int value) {
        if (value < MIN_SKILL) return MIN_SKILL;
        if (value > MAX_SKILL) return MAX_SKILL;
        return value;
    }
}
