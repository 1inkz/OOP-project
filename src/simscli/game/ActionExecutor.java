package simscli.game;

import simscli.actions.Action;
import simscli.sims.Sim;

/**
 * Executes actions in the game context.
 * Encapsulates action execution logic and dead sim cleanup.
 */
public class ActionExecutor {
    private final GameLogger logger;

    public ActionExecutor(GameLogger logger) {
        this.logger = logger;
    }

    /**
     * Performs an action for the active sim.
     * Returns result message and handles sid cleanup.
     */
    public String performAction(Sim activeSim, Action action, Game game) {
        if (activeSim == null) return "No active sim.";
        if (!activeSim.isAlive()) return activeSim.getName() + " is no longer in the simulation.";

        String msg = action.perform(activeSim, new GameContext(game));
        
        return msg;
    }

    /**
     * Changes a sim's job.
     */
    public String changeJob(Sim activeSim, String jobName) {
        if (activeSim == null) return "No active sim.";

        try {
            simscli.jobs.Job job = simscli.jobs.JobFactory.create(jobName);
            activeSim.setJob(job);

            String[] workLocations = job.getWorkLocations();
            if (workLocations == null || workLocations.length == 0) {
                return activeSim.getName() + " is now a " + activeSim.getJobName() + ".";
            }

            return activeSim.getName() + " is now a " + activeSim.getJobName()
                    + ". You can work at: " + formatLocationList(workLocations) + ".";
        } catch (IllegalArgumentException e) {
            return "Unknown job. Try: Chef / Doctor / Engineer / Influencer";
        }
    }

    private String formatLocationList(String[] keys) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                sb.append(" or ");
            }
            sb.append(formatLocationName(keys[i]));
        }

        return sb.toString();
    }

    private String formatLocationName(String key) {
        return switch (key.toLowerCase()) {
            case "restaurant" -> "Restaurant";
            case "hospital" -> "Hospital";
            case "bank" -> "Bank";
            case "park" -> "Park";
            case "home" -> "Home";
            case "street" -> "Street";
            default -> key.substring(0, 1).toUpperCase() + key.substring(1);
        };
    }
}
