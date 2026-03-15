package simscli.stats;

import java.util.EnumMap;
import java.util.Map;

public final class Skills {
    private static final int MIN_SKILL = 0;
    private static final int MAX_SKILL = 100;

    private final EnumMap<SkillType, BoundedStat> skills = new EnumMap<>(SkillType.class);

    public Skills() {
        for (SkillType type : SkillType.values()) {
            skills.put(type, new BoundedStat(0));
        }
    }

    public int get(SkillType type) {
        validateType(type);
        return skills.get(type).get();
    }

    public void set(SkillType type, int value) {
        validateType(type);
        skills.get(type).set(clamp(value));
    }

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

    public boolean isMaxed(SkillType type) {
        return get(type) >= MAX_SKILL;
    }

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

    public Map<SkillType, Integer> snapshot() {
        EnumMap<SkillType, Integer> copy = new EnumMap<>(SkillType.class);
        for (Map.Entry<SkillType, BoundedStat> entry : skills.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().get());
        }
        return copy;
    }

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
