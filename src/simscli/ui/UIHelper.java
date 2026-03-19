package simscli.ui;

import simscli.SaveGame;
import simscli.stats.NeedType;
import simscli.sims.Sim;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for UI formatting and color management.
 */
public class UIHelper {
	
    public final String RED = "\u001B[31m";
    public final String GREEN = "\u001B[32m";
    public final String YELLOW = "\u001B[33m";
    public final String BLUE = "\u001B[34m";
    public final String PURPLE = "\u001B[35m";
    public final String DARK_RED = "\u001B[91m";
    public final String CYAN = "\u001B[36m";
    public final String RESET = "\u001B[0m";
    
    private final Input in;

    public UIHelper(Input in) {
        this.in = in;
    }
    
    /**
     * Gets color code for a need value for console display.
     * @param value the need value (0-100)
     * @return colored number string
     */
    public String getNeedColor(int value) {
        if (value >= 60) {
            return GREEN + String.valueOf(value) + RESET;
        } else if (value >= 21) {
            return YELLOW + String.valueOf(value) + RESET;
        } else {
            return RED + value + " (DANGER)" + RESET;
        }
    }
    
    /**
     * Gets warning messages for all critical needs (value <= 20).
     * @param sim the Sim to check
     * @return list of warning messages for critical needs
     */
    public List<String> getCriticalNeedWarnings(Sim sim) {
        List<String> warnings = new ArrayList<>();
        
        if (sim.getNeeds().get(NeedType.HUNGER) <= 20) {
            warnings.add("❌ HUNGER CRITICAL: You need to eat soon!");
        }
        if (sim.getNeeds().get(NeedType.ENERGY) <= 20) {
            warnings.add("❌ ENERGY CRITICAL: You need to sleep soon!");
        }
        if (sim.getNeeds().get(NeedType.HYGIENE) <= 20) {
            warnings.add("❌ HYGIENE CRITICAL: You need to take a shower soon!");
        }
        if (sim.getNeeds().get(NeedType.SOCIAL) <= 20) {
            warnings.add("❌ SOCIAL CRITICAL: You need to socialize soon!");
        }
        if (sim.getNeeds().get(NeedType.FUN) <= 20) {
            warnings.add("❌ FUN CRITICAL: You need to have fun soon!");
        }
        if (sim.getNeeds().get(NeedType.BLADDER) <= 20) {
            warnings.add("❌ BLADDER CRITICAL: You need to use the bathroom soon!");
        }
        
        return warnings;
    }
    
    /**
     * Prints a formatted title with colored border.
     * @param title the title text
     * @param color ANSI color code
     */
    public void printDynamicTitle(String title, String color) {
        int width = title.length() + 10;

        System.out.println(color + "=".repeat(width) + RESET);
        System.out.println(color + " ".repeat(5) + title + RESET);
        System.out.println(color + "=".repeat(width) + RESET);
    }
    
    /**
     * Prompts user to confirm starting a new game (losing save).
     * @return true if user confirms
     */
    public boolean confirmStartNewGame() {
        boolean hasValidSave = SaveGame.hasValidSaveData();

        if (!hasValidSave) {
            return true;
        }

        System.out.println(RED + "\nWarning: Starting a New Game will cause you to lose all progress from your previous saved game." + RESET);
        String confirm = in.line("Do you still want to continue? (y/n): ");

        return confirm.equalsIgnoreCase("y");
    }

}
