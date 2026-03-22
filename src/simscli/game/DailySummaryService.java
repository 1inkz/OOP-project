package simscli.game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import simscli.sims.Sim;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

/**
 * Builds and logs end-of-day summaries for each sim.
 * Tracks per-day snapshots so summaries can show deltas and tips.
 */
public final class DailySummaryService {
    private final Map<Sim, DaySnapshot> snapshots = new HashMap<>();

    public void captureDayStart(int dayNumber, List<Sim> sims) {
        for (Sim sim : sims) {
            if (!sim.isAlive()) {
                continue;
            }

            DaySnapshot snapshot = snapshots.get(sim);
            if (snapshot == null || snapshot.dayNumber != dayNumber) {
                snapshots.put(sim, DaySnapshot.from(dayNumber, sim));
            }
        }
    }

    public void logEndOfDay(int finishedDay, List<Sim> sims, GameLogger logger) {
        for (Sim sim : sims) {
            DaySnapshot start = snapshots.get(sim);
            if (start == null || start.dayNumber != finishedDay) {
                continue;
            }

            String summary = buildSummary(sim, start);
            if (!summary.isEmpty()) {
                logger.info(summary);
            }
        }
    }

    private String buildSummary(Sim sim, DaySnapshot start) {
        int currentCash = sim.getSimcoin();
        int currentBank = sim.getBankDeposit();
        int currentLoan = sim.getLoanAmount();

        int wealthDelta = (currentCash + currentBank) - (start.cash + start.bankDeposit);
        int loanDelta = currentLoan - start.loanAmount;

        String skillDelta = buildSkillDelta(sim, start.skills);
        String events = buildMajorEvents(sim, wealthDelta, loanDelta);
        String tips = buildTips(sim);

        boolean hasSkillGains = !"No gains".equals(skillDelta);
        boolean hasEvents = !"None".equals(events);
        boolean hasActionableTip = sim.getNeeds().get(NeedType.HUNGER) <= 40
            || sim.getNeeds().get(NeedType.ENERGY) <= 40
            || sim.getLoanAmount() > 0
            || sim.getSimcoin() < 120
            || sim.getOwnedCar() != null;

        if (wealthDelta == 0
            && !hasSkillGains
            && !hasEvents
            && !hasActionableTip
            && sim.getLoanAmount() == 0
            && sim.getOwnedHotel() == null) {
            return "";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("[Day ").append(start.dayNumber).append(" Summary] ").append(sim.getName()).append(" | ");
        summary.append("Money ")
                .append(formatSigned(wealthDelta))
                .append(" (Cash $")
                .append(currentCash)
                .append(", Bank $")
                .append(currentBank)
                .append(") | ");
        summary.append("Skills ").append(skillDelta).append(" | ");
        summary.append("Events ").append(events).append(" | ");
        summary.append("Tip ").append(tips);
        return summary.toString();
    }

    private String buildSkillDelta(Sim sim, Map<SkillType, Integer> beforeSkills) {
        StringJoiner joiner = new StringJoiner(", ");
        Map<SkillType, Integer> now = sim.getAllSkillLevels();

        for (SkillType type : SkillType.values()) {
            int before = beforeSkills.getOrDefault(type, 0);
            int after = now.getOrDefault(type, 0);
            int delta = after - before;
            if (delta > 0) {
                joiner.add(type.displayName() + " +" + delta);
            }
        }

        if (joiner.length() == 0) {
            return "No gains";
        }
        return joiner.toString();
    }

    private String buildMajorEvents(Sim sim, int wealthDelta, int loanDelta) {
        List<String> events = new ArrayList<>();

        if (wealthDelta >= 250) {
            events.add("Strong profit day");
        } else if (wealthDelta <= -150) {
            events.add("Heavy spending day");
        }

        if (loanDelta > 0) {
            events.add("Debt increased by $" + loanDelta);
        } else if (loanDelta < 0) {
            events.add("Debt reduced by $" + (-loanDelta));
        }

        if (sim.getNeeds().get(NeedType.HUNGER) <= 20 || sim.getNeeds().get(NeedType.ENERGY) <= 20) {
            events.add("Critical survival needs triggered");
        }

        if (sim.getNeeds().get(NeedType.HYGIENE) <= 20) {
            events.add("Hygiene stayed dangerously low");
        }

        if (sim.getOwnedHotel() != null) {
            events.add("Hotel operations ran at level " + sim.getOwnedHotelLevel());
        }

        if (events.isEmpty()) {
            return "None";
        }
        return String.join("; ", events);
    }

    private String buildTips(Sim sim) {
        List<String> tips = new ArrayList<>();

        if (sim.getNeeds().get(NeedType.HUNGER) <= 40) {
            tips.add("Eat before long travel");
        }
        if (sim.getNeeds().get(NeedType.ENERGY) <= 40) {
            tips.add("Sleep earlier to avoid midnight drain");
        }
        if (sim.getLoanAmount() > 0) {
            tips.add("Repay part of your loan soon");
        }
        if (sim.getSimcoin() < 120) {
            tips.add("Take one paid shift tomorrow");
        }
        if (sim.getOwnedCar() != null) {
            tips.add("Keep cash ready for car maintenance");
        }

        if (tips.isEmpty()) {
            return "Train job skills for faster promotions";
        }

        return tips.get(0);
    }

    private String formatSigned(int amount) {
        if (amount > 0) {
            return "+$" + amount;
        }
        if (amount < 0) {
            return "-$" + (-amount);
        }
        return "$0";
    }

    private static final class DaySnapshot {
        private final int dayNumber;
        private final int cash;
        private final int bankDeposit;
        private final int loanAmount;
        private final Map<SkillType, Integer> skills;

        private DaySnapshot(int dayNumber, int cash, int bankDeposit, int loanAmount, Map<SkillType, Integer> skills) {
            this.dayNumber = dayNumber;
            this.cash = cash;
            this.bankDeposit = bankDeposit;
            this.loanAmount = loanAmount;
            this.skills = skills;
        }

        private static DaySnapshot from(int dayNumber, Sim sim) {
            Map<SkillType, Integer> copy = new EnumMap<>(SkillType.class);
            copy.putAll(sim.getAllSkillLevels());
            return new DaySnapshot(dayNumber, sim.getSimcoin(), sim.getBankDeposit(), sim.getLoanAmount(), copy);
        }
    }
}