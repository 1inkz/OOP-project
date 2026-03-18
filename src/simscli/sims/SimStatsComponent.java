package simscli.sims;

import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.stats.Needs;
import simscli.stats.SkillType;
import simscli.stats.Skills;

/**
 * Manages all Sim statistics: needs, skills, and death conditions.
 * Encapsulates stat logic for better testability and reusability.
 */
public class SimStatsComponent {
    private final Needs needs;
    private final Skills skills;
    private boolean alive = true;

    public SimStatsComponent() {
        this.needs = new Needs();
        this.skills = new Skills();
    }

    // Needs
    public Needs getNeeds() {
        return needs;
    }

    /**
     * Calculates average mood from all need values.
     * @return average need value
     */
    public int moodScore() {
        int sum = 0;
        for (NeedType t : NeedType.values()) {
            sum += needs.get(t);
        }
        return sum / NeedType.values().length;
    }

    public boolean isCritical(NeedType type) {
        return needs.isCritical(type);
    }

    // Skills
    public Skills getSkills() {
        return skills;
    }

    public int getSkillLevel(SkillType type) {
        return skills.get(type);
    }

    public int gainSkill(SkillType type, int amount) {
        return skills.gain(type, amount);
    }

    public java.util.Map<SkillType, Integer> getAllSkillLevels() {
        return new java.util.EnumMap<>(skills.snapshot());
    }

    public void setAllSkillLevels(java.util.Map<SkillType, Integer> values) {
        skills.loadFrom(values);
    }

    // Life/Death
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Applies an effect's need deltas and checks death.
     * @param effect the effect to apply
     */
    public void applyEffect(Effect effect) {
        for (java.util.Map.Entry<NeedType, Integer> entry : effect.deltas().entrySet()) {
            needs.add(entry.getKey(), entry.getValue());
        }
        checkDeathConditions();
    }

    private void checkDeathConditions() {
        for (NeedType t : NeedType.values()) {
            if (needs.isZero(t)) {
                alive = false;
                return;
            }
        }
    }
}
