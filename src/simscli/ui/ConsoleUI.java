package simscli.ui;

import java.util.ArrayList;
import java.util.List;
import simscli.ExitGuard;
import simscli.SaveGame;
import simscli.game.Game;
import simscli.game.GameLogger;
import simscli.sims.Sim;

/**
 * Main console-based UI for the game.
 * Manages the game loop and delegates to specialized UI managers.
 */
public final class ConsoleUI {

    private final Game game;
    private final Input in = new Input();
    private UIHelper uiHelper;
    GameLogger gameLogger;
    private SimUIManager simUIManager;
    private MenuUIManager menuUIManager;
    private BusinessUIManager businessUIManager;
    
    public boolean isGameReset = false;

    public ConsoleUI(Game game) {
        this.game = game;
        this.game.setUIInput(in);  // Inject UI input for action DI
        this.uiHelper = new UIHelper(in);
        this.menuUIManager = new MenuUIManager(game, in, uiHelper);
        this.simUIManager = new SimUIManager(game, in, uiHelper, menuUIManager);
        this.businessUIManager = new BusinessUIManager(game, in, uiHelper);
    }

    /**
     * Starts the main game loop with real-time auto-advance.
     * Handles active Sim selection and cleanup.
     */
    public void run() {
    	menuUIManager.showInitialMenu();

        // Main game loop (real-time auto-advance)
        long lastTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();
            double deltaSeconds = (currentTime - lastTime) / 1000.0;
            lastTime = currentTime;

            // Auto-advance time (GameClock's design)
            game.autoAdvanceRealTime(deltaSeconds);
            game.advanceTimeForAction();
            
            // set game to modified
            game.markGameModified();

            Sim activeSim = game.activeSim();
            if (activeSim == null) {
                if (game.sims().isEmpty()) {
                    System.out.println("All Sims are gone. Game restarted! Create a new Sim.");
                    game.resetGame();
                    SaveGame.clearSaveFile();
                    menuUIManager.showSimManagementMenu();
                } else {
                    System.out.println("[GAME] Your active Sim is no longer available. Please select another Sim.");
                    game.setActiveSim(-1);
                    simUIManager.selectExistingSim();
                }
                continue;
            }
            activeSim.setLastInactiveDays(game.getClock().getDayNumber());

            String currentLocation = activeSim.getLocation().name();
            String currentLocationOption = "View [" + currentLocation + "] Actions Menu";
            String jobless = activeSim.getJobName();

            uiHelper.printDynamicTitle("SIMS GAME - " + activeSim.getName() + " Main Menu", uiHelper.DARK_RED);
            System.out.println(uiHelper.PURPLE + "Time: Day " + activeSim.getPersonalDay(game) + " " + game.timeString() + uiHelper.RESET + "\n");
            
            // Display critical need warnings automatically during gameplay
            List<String> warnings = uiHelper.getCriticalNeedWarnings(activeSim);
            if (!warnings.isEmpty()) {
                System.out.println(uiHelper.RED + "!!  WARNING - CRITICAL NEEDS !!" + uiHelper.RESET);
                for (String warning : warnings) {
                    System.out.println(uiHelper.RED + warning + uiHelper.RESET);
                }
                System.out.println();
            }

            List<String> menuOptions = new ArrayList<>();

            menuOptions.add("View Sims Status");
            menuOptions.add("Travel to Location");

            if (!currentLocation.equals("Street")) {
                menuOptions.add(currentLocationOption);
            }

            if (jobless.equals("Jobless")) {
                menuOptions.add("Find Job");
            } else {
                menuOptions.add("Change Job");
            }

            menuOptions.add("Asset Operations [Buy/Sell Car/House/Hotel]");
            menuOptions.add("Pass Time (1 Hour)");
            menuOptions.add("Return to Sims Management Menu");
            menuOptions.add("Quit Game");

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(uiHelper.BLUE + (i + 1) + ") " + menuOptions.get(i) + uiHelper.RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            if (selectedOption.equals(currentLocationOption)) {
                selectedOption = "Location Action Menu";
            } else if (selectedOption.equals("Find Job") || selectedOption.equals("Change Job")) {
                selectedOption = "Job";
            }

            switch (selectedOption) {
                case "View Sims Status":
                	simUIManager.printSimStatus(activeSim);
                    break;
                case "Travel to Location":
                	businessUIManager.showTravelToLocationMenu();
                    break;
                case "Location Action Menu":
                	businessUIManager.showDoLocationActionsMenu(activeSim.getLocation());
                    break;
                case "Job":
                	businessUIManager.showChangeJobMenu();
                    break;
                case "Asset Operations [Buy/Sell Car/House/Hotel]":
                	businessUIManager.showAssetOperationsMenu();
                    break;
                case "Pass Time (1 Hour)":
                    game.advanceTimeForAction();
                    break; // Pass time (1 hour)
                case "Return to Sims Management Menu":
                	menuUIManager.showSimManagementMenu();
                    break;
                case "Quit Game":

                    String confirm = in.stringOptions("Save current progress before quitting? (y/n): ", "y", "n");

                    if (confirm.equalsIgnoreCase("y")) {
                        SaveGame.saveGame(game);
                        System.out.println("Game saved.");
                    }

                    ExitGuard.markNormalExit();
                    System.out.println(uiHelper.DARK_RED + "Goodbye!" + uiHelper.RESET);
                    game.shutdown();
                    return;
            }
            
            // Dead sim rules
            List<Sim> deadSims = new ArrayList<>();
            boolean anyAlive = false;
            for (Sim sim : game.sims()) {
                if (!sim.isAlive()) {
                	sim.setAlive(false);
                	deadSims.add(sim);
                } else {
                	anyAlive = true;
                } 
            }
            
            if (!deadSims.isEmpty()) {
                for (Sim deadSim : deadSims) {                    
                    if (deadSim == activeSim) {
                        activeSim = null;
                    }
                }
                game.cleanupDeadSims();
            }
            
            if (activeSim == null || !activeSim.isAlive()) {
                if (anyAlive) {
                    game.setActiveSim(-1);
                    simUIManager.selectExistingSim();
                    activeSim = game.activeSim();
                } else {
                    System.out.println("All Sims are gone. Game restarted! Create a new Sim.");
                    game.resetGame();
                    SaveGame.clearSaveFile();
                    menuUIManager.showSimManagementMenu();
                }
            } else if (!deadSims.isEmpty()) {
            	game.setActiveSim(activeSim.getActiveSimIndex() - 1);
            }
            continue;
        }
    }
    
}
