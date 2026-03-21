package simscli.ui;

import simscli.ExitGuard;
import simscli.game.Game;
import simscli.sims.Sim;
import simscli.SaveGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages main menus including start, Sim management, and sub-menus.
 */
public class MenuUIManager {
	
    private final Game game;
    private final Input in;
    private final UIHelper uiHelper;
    private final SimUIManager simUIManager;

    public boolean isGameReset = false;

    public MenuUIManager(Game game, Input in, UIHelper uiHelper) {
        this.game = game;
        this.in = in;
        this.uiHelper = uiHelper;
        this.simUIManager = new SimUIManager(game, in, uiHelper, this);
    }
    
    /**
     * Displays the initial start menu with continue/new game options.
     */
    public void showInitialMenu() {
        boolean initialMenuRunning = true;

        while (initialMenuRunning) {

            List<String> menuOptions = new ArrayList<>();
            menuOptions.add("New Game");

            boolean hasValidSave = SaveGame.hasValidSaveData();
            if (game.isGameModified() && !hasValidSave) {
                
            } else if (game.isGameModified() || hasValidSave) {
            	menuOptions.add("Continue Game");
            }
            
            menuOptions.add("Quit Game");

            uiHelper.printDynamicTitle("SIMS GAME - START MENU", uiHelper.DARK_RED);

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(uiHelper.BLUE + (i + 1) + ") " + menuOptions.get(i) + uiHelper.RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            switch (selectedOption) {
                case "New Game":
                    if (!uiHelper.confirmStartNewGame()) {
                        System.out.println(uiHelper.YELLOW + "New Game cancelled. Returning to Start Menu." + uiHelper.RESET);
                        break;
                    }

                    game.resetGame();
                    SaveGame.clearSaveFile();
                    isGameReset = true;

                    System.out.println(uiHelper.GREEN + "New Game started!" + uiHelper.RESET);
                    showSimManagementMenu();
                    initialMenuRunning = false;
                    break;

                case "Continue Game":
                    if (hasValidSave) {
                    	SaveGame.loadGame(game);
                    	System.out.println(uiHelper.GREEN + "Successfully loaded last game!\n" + uiHelper.RESET);
                    }
                    showSimManagementMenu();
                    initialMenuRunning = false;
                    
                    break;
                case "Quit Game":
                    if (game.isGameModified()) {
                        String confirm = in.line("Save current progress before quitting? (y/n): ");

                        if (confirm.equalsIgnoreCase("y")) {
                            SaveGame.saveGame(game);
                            System.out.println("Game saved.");
                        }
                    }

                    ExitGuard.markNormalExit();
                    System.out.println(uiHelper.DARK_RED + "Goodbye!" + uiHelper.RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println(uiHelper.RED + "Invalid Option" + uiHelper.RESET);
                    break;
            }
        }
    }
    
    /**
     * Displays Sim management menu for creation and selection.
     */
    public void showSimManagementMenu() {
        boolean simMenuRunning = true;
        

        while (simMenuRunning) {

            List<Sim> sims = game.sims();
            boolean hasSims = !sims.isEmpty();
            Sim activeSim = game.activeSim();

            uiHelper.printDynamicTitle("SIMS GAME - Sims Management Menu", uiHelper.DARK_RED);

            List<String> menuOptions = new ArrayList<>();

            menuOptions.add("Create New Sim");

            if (hasSims) {
                menuOptions.add("Select Existing Sim");
            }

            String activeSimsOption = "";
            if (activeSim != null) {
                activeSimsOption = "Enter [" + activeSim.getName() + "] Main Menu (Last Played SIM)";
                if (hasSims) {
                    menuOptions.add(activeSimsOption);
                }
            }

            menuOptions.add("Return to Start Menu");
            menuOptions.add("Quit Game");

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(uiHelper.BLUE + (i + 1) + ") " + menuOptions.get(i) + uiHelper.RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            if (selectedOption == activeSimsOption) {
                selectedOption = "Enter Action Menu";
            }

            switch (selectedOption) {
                case "Create New Sim":
                	simUIManager.createNewSim();
                	activeSim = game.activeSim();
                    break;
                case "Select Existing Sim":
                	simUIManager.selectExistingSim();
                	activeSim = game.activeSim();
                    if (activeSim != null) { simMenuRunning = false; }
                    break;
                case "Enter Action Menu":
                    if (activeSim != null) {
                    	game.markGameModified();
                        simMenuRunning = false;
                    }
                    break;
                case "Return to Start Menu":
                    showInitialMenu();
                    break;
                case "Quit Game":
                	if (game.isGameModified()) {
                        String confirm = in.line("Save current progress before quitting? (y/n): ");

                        if (confirm.equalsIgnoreCase("y")) {
                            SaveGame.saveGame(game);
                            System.out.println("Game saved.");
                        }
                	}

                    ExitGuard.markNormalExit();
                    System.out.println(uiHelper.DARK_RED + "Goodbye!" + uiHelper.RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println(uiHelper.RED + "Invalid Option!" + uiHelper.RESET);

                    break;
            }
        }
    }
    



}
