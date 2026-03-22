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

    private static final String[] FINANCE_FLAVORS = {
            "[Vibe] Smart money move.",
            "[Vibe] Ledger looks healthier.",
            "[Vibe] Financial control improved."
    };

    private static final String[] REST_FLAVORS = {
            "[Vibe] Recovery helped.",
            "[Vibe] You feel steadier.",
            "[Vibe] Good reset."
    };

    private static final String[] COMMERCE_FLAVORS = {
            "[Vibe] Portfolio evolving.",
            "[Vibe] Long game improved.",
            "[Vibe] Strong market decision."
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
                || m.contains("no longer in the simulation");
    }

    private String[] chooseFlavorBank(String lowerMessage) {
        if (lowerMessage.contains("earned") || lowerMessage.contains("worked")) {
            return WORK_FLAVORS;
        }
        if (lowerMessage.contains("deposited") || lowerMessage.contains("withdrew") || lowerMessage.contains("loan")
                || lowerMessage.contains("repay")) {
            return FINANCE_FLAVORS;
        }
        if (lowerMessage.contains("sleep") || lowerMessage.contains("nap") || lowerMessage.contains("rest")) {
            return REST_FLAVORS;
        }
        if (lowerMessage.contains("bought") || lowerMessage.contains("sold")) {
            return COMMERCE_FLAVORS;
        }
        return null;
    }
}