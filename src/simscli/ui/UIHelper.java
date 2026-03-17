package simscli.ui;

import simscli.SaveGame;

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
    
    public String getNeedColor(int value) {
        if (value >= 60) {
            return GREEN + String.valueOf(value) + RESET;
        } else if (value >= 21) {
            return YELLOW + String.valueOf(value) + RESET;
        } else {
            return RED + value + " (DANGER)" + RESET;
        }
    }
    
    public void printDynamicTitle(String title, String color) {
        int width = title.length() + 10;

        System.out.println(color + "=".repeat(width) + RESET);
        System.out.println(color + " ".repeat(5) + title + RESET);
        System.out.println(color + "=".repeat(width) + RESET);
    }
    
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
