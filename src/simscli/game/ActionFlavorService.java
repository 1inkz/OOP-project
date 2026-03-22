package simscli.game;

import simscli.sims.Sim;

/**
 * Adds lightweight flavor lines to common action outputs.
 * Keeps core action messages intact while improving moment-to-moment UX.
 */
public final class ActionFlavorService {
    private static final String[] WORK_FLAVORS = {
            "[Vibe] Productive shift.",
            "[Vibe] Promotion momentum rising.",
            "[Vibe] Solid work rhythm today."
    };

    public String decorate(String message, Sim sim, GameClock clock) {
        if (message == null || message.isBlank() || sim == null || clock == null) {
            return message;
        }

        if (isSystemOrErrorMessage(message)) {
            return message;
        }

        String[] bank = chooseFlavorBank(message.toLowerCase());
        if (bank == null) {
            return message;
        }

        int idx = Math.floorMod(sim.getName().hashCode() + clock.getDayNumber() + clock.getMinuteOfDay(), bank.length);
        return message + " " + bank[idx];
    }

    private boolean isSystemOrErrorMessage(String message) {
        String m = message.toLowerCase();
        return m.contains("no active sim")
                || m.contains("unknown")
                || m.contains("invalid")
                || m.contains("cannot enter")
                || m.contains("no longer in the simulation")
                || m.contains("limit reached")
                || m.contains("maximum amount")
                || m.contains("failed")
                || m.contains("arrived at");
    }

    private String[] chooseFlavorBank(String lowerMessage) {
        if (lowerMessage.contains("earned") || lowerMessage.contains("worked")) {
            return WORK_FLAVORS;
        }
        return null;
    }
}