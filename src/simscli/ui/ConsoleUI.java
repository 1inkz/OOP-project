package simscli.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import simscli.ExitGuard;
import simscli.SaveGame;
import simscli.actions.Action;
import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.House;
import simscli.game.Game;
import simscli.location.Location;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

public final class ConsoleUI {

    private final Game game;
    private final Input in = new Input();
    private boolean tutorialShown = false;
    private boolean isGameLoaded = false;
    public boolean isGameReset = false;

    public ConsoleUI(Game game) {
        this.game = game;
        this.game.setUIInput(in);  // Inject UI input for action DI
    }

    public void run() {
        showInitialMenu();

        // Main game loop (real-time auto-advance)
        long lastTime = System.currentTimeMillis();
        while (true) {
            long currentTime = System.currentTimeMillis();
            double deltaSeconds = (currentTime - lastTime) / 1000.0;
            lastTime = currentTime;

            // Auto-advance time (GameClock's design)
            game.autoAdvanceRealTime(deltaSeconds);

            Sim activeSim = game.activeSim();

            if (activeSim == null || !activeSim.isAlive()) {

                System.out.println("\nYour Sim has left the simulation.");

                // check if other sims still exist
                boolean anyAlive = false;

                for (Sim sim : game.sims()) {
                    if (sim.isAlive()) {
                        anyAlive = true;
                        break;
                    }
                }

                if (anyAlive) {
                    System.out.println("Select another Sim to continue.");
                } else {
                    System.out.println("All Sims are gone. Create a new Sim.");
                }

                in.line("Press Enter to continue...");
                showSimManagementMenu();

                continue;
            }

            String currentLocation = activeSim.getLocation().name();
            String currentLocationOption = "View [" + currentLocation + "] Actions Menu";
            String jobless = activeSim.getJobName();

            printDynamicTitle("SIMS GAME - " + activeSim.getName() + " Main Menu");
            System.out.println(PURPLE + "Time: Day " + activeSim.getPersonalDay(game) + game.timeString() + RESET + "\n");

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

            menuOptions.add("Asset Operations [Buy/Sell Car/House]");
            menuOptions.add("Pass Time (1 Hour)");
            menuOptions.add("View FAQ");
            menuOptions.add("Return to Sims Management Menu");
            menuOptions.add("Quit Game");

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(BLUE + (i + 1) + ") " + menuOptions.get(i) + RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            if (selectedOption == currentLocationOption) {
                selectedOption = "Location Action Menu";
            } else if (selectedOption.equals("Find Job") || selectedOption.equals("Change Job")) {
                selectedOption = "Job";
            }

            switch (selectedOption) {
                case "View Sims Status":
                    printSimStatus(activeSim);
                    break;
                case "Travel to Location":
                    showTravelToLocationMenu();
                    break;
                case "Location Action Menu":
                    showDoLocationActionsMenu(activeSim.getLocation());
                    break;
                case "Job":
                    showChangeJobMenu();
                    break;
                case "Asset Operations [Buy/Sell Car/House]":
                    showAssetOperationsMenu();
                    break;
                case "Pass Time (1 Hour)":
                    game.advanceTimeForAction();
                    break; // Pass time (1 hour)
                case "View FAQ":
                    showFAQMenu();
                    break;
                case "Return to Sims Management Menu":
                    showSimManagementMenu();
                    break;
                case "Quit Game":

                    String confirm = in.line("Save current progress before quitting? (y/n): ");

                    if (confirm.equalsIgnoreCase("y")) {
                        SaveGame.saveGame(game);
                        System.out.println("Game saved.");
                    }

                    ExitGuard.markNormalExit();
                    System.out.println(DARK_RED + "Goodbye!" + RESET);
                    game.shutdown();
                    return;
            }

            if (game.sims().isEmpty()) {
                System.out.println("\nAll Sims are gone.");
                in.line("Press Enter to return to the Sims Management Menu...");
                showSimManagementMenu();
                continue;
            }
        }
    }

    // Start: General
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String DARK_RED = "\u001B[91m";
    private static final String CYAN = "\u001B[36m";

    private static final String RESET = "\u001B[0m";

    private void showTutorial() {

        printDynamicTitle("Tutorial 1/3");
        System.out.println("1. All actions consume 1 hour of in-game time");
        System.out.println("2. Any need hitting 0 will eliminate your Sim");
        System.out.println("3. Time advances in real-time (1 sec = 1 min)");
        in.line("\nPress Enter to continue...\n");

        printDynamicTitle("Tutorial 2/3");
        System.out.println("1. Buy a Car to avoid travel costs (Hunger/Energy loss)");
        System.out.println("2. Buy a House to unlock Home hygiene actions (Shower/Brush Teeth)");
        System.out.println("3. 22:00: Must be in Park (no house) or Home (house) to avoid fainting");
        in.line("\nPress Enter to continue...\n");

        printDynamicTitle("Tutorial 3/3");
        System.out.println("1. Bank deposits earn 0.05% interest daily (midnight)");
        System.out.println("2. Max loan limit: $" + simscli.bank.BankingSystem.getLoanLimit());
        System.out.println("3. Dine Out at Restaurant costs $25 and restores Hunger/Social");
        in.line("\nPress Enter to continue...\n");
    }

    private String getNeedColor(int value) {
        if (value >= 60) {
            return GREEN + String.valueOf(value) + RESET;
        } else if (value >= 21) {
            return YELLOW + String.valueOf(value) + RESET;
        } else {
            return RED + value + " (DANGER)" + RESET;
        }
    }

    private void printDynamicTitle(String title) {
        int width = title.length() + 10;

        System.out.println(DARK_RED + "=".repeat(width) + RESET);
        System.out.println(DARK_RED + " ".repeat(5) + title + RESET);
        System.out.println(DARK_RED + "=".repeat(width) + RESET);
    }
    // End: General

    private boolean confirmStartNewGame() {
        boolean hasValidSave = SaveGame.hasValidSaveData();

        if (!hasValidSave) {
            return true;
        }

        System.out.println(RED + "\nWarning: Starting a New Game will cause you to lose all progress from your previous saved game." + RESET);
        String confirm = in.line("Do you still want to continue? (y/n): ");

        return confirm.equalsIgnoreCase("y");
    }

    // Start: Sims
    private void createNewSim() {

        printDynamicTitle("SIMS GAME - Create New Sims");

        String name = in.line("Enter Sims name (or type 'cancel' to return): ");
        if (name.equalsIgnoreCase("cancel")) {
            System.out.println(BLUE + "Returning to Sims Management Menu" + RESET);
            return;
        }

        if (name.trim().isEmpty()) {
            System.out.println(RED + "Name cannot be empty!" + RESET);

            createNewSim();
            return;
        }

        System.out.println(BLUE + "\nSelect Sim Type:" + RESET);
        System.out.println(BLUE + "1) Child" + RESET);
        System.out.println(BLUE + "2) Adult" + RESET);
        System.out.println(BLUE + "3) Elder" + RESET);
        int t = in.intRange("Choose: ", 1, 3);
        SimType type = (t == 1) ? SimType.CHILD : (t == 2) ? SimType.ADULT : SimType.ELDER;

        game.createSim(name, type);
        // Show tutorial once when new sims created
        if (!tutorialShown) {
            showTutorial();
            tutorialShown = true;
        }

        List<Sim> sims = game.sims();
        for (int i = 0; i < sims.size(); i++) {
            Sim sim = sims.get(i);
            if (sim.getName() == name) {
                game.setActiveSim(i);
            };
        }

        System.out.println(GREEN + "Created new Sim: " + name + " (" + type + ")" + RESET);

    }

    private void selectExistingSim() {

        List<Sim> sims = game.sims();
        printDynamicTitle("SIMS GAME - Select Existing Sims");

        game.cleanupDeadSims();

        for (int i = 0; i < sims.size(); i++) {
            Sim sim = sims.get(i);
            String status = sim.isAlive() ? GREEN + "Alive" + RESET : RED + "Gone" + RESET;
            System.out.println(BLUE + (i + 1) + ") " + sim.getName() + " (" + sim.getType() + ") - " + status + RESET);
        };

        System.out.println(BLUE + (sims.size() + 1) + ") Return to Sims Management Menu" + RESET);

        int choice = in.intRange("\nChoose Sims (1-" + (sims.size() + 1) + "): ", 1, sims.size() + 1);
        if (choice == sims.size() + 1) {
            showSimManagementMenu();
            return;
        }

        int simIndex = choice - 1;
        game.setActiveSim(simIndex);
        System.out.println(GREEN + "Active Sims changed to: " + game.activeSim().getName() + RESET);
    }

    private void printSimStatus(Sim sim) {

        String needsOutput = "Hunger: " + getNeedColor(sim.getNeeds().get(NeedType.HUNGER)) + " | "
                + "Energy: " + getNeedColor(sim.getNeeds().get(NeedType.ENERGY)) + " | "
                + "Hygiene: " + getNeedColor(sim.getNeeds().get(NeedType.HYGIENE)) + " | "
                + "Social: " + getNeedColor(sim.getNeeds().get(NeedType.SOCIAL)) + " | "
                + "Fun: " + getNeedColor(sim.getNeeds().get(NeedType.FUN)) + " | "
                + "Bladder: " + getNeedColor(sim.getNeeds().get(NeedType.BLADDER));

        String skillsOutput
                = "Cooking: " + sim.getSkillLevel(SkillType.COOKING) + " | "
                + "Cleaning: " + sim.getSkillLevel(SkillType.CLEANING) + " | "
                + "Charisma: " + sim.getSkillLevel(SkillType.CHARISMA) + " | "
                + "Fitness: " + sim.getSkillLevel(SkillType.FITNESS) + "\n"
                + "Intelligence: " + sim.getSkillLevel(SkillType.INTELLIGENCE) + " | "
                + "Creativity: " + sim.getSkillLevel(SkillType.CREATIVITY) + " | "
                + "Gaming: " + sim.getSkillLevel(SkillType.GAMING) + " | "
                + "Work Ethic: " + sim.getSkillLevel(SkillType.WORK_ETHIC);

        int width = (needsOutput.length()) / 4;

        printDynamicTitle(" ".repeat(width) + sim.getName() + " - Status" + " ".repeat(width));

        System.out.println("Name: " + sim.getName() + " | Type: " + sim.getType());
        System.out.println("Job: " + sim.getJobName() + " (Level " + sim.getJobLevel() + ")");
        System.out.println("Location: " + sim.getLocation().name());
        System.out.println("Simcoin: $" + sim.getSimcoin() + " | Bank Savings: $" + sim.getBankingSystem().getDeposit() + " | Loan: $" + sim.getBankingSystem().getLoanAmount());
        System.out.println("Assets: Car = " + (sim.getOwnedCar() != null ? "Yes" : "No") + " | House = " + (sim.getOwnedHouse() != null ? "Yes" : "No") + " | Assets Loan Day: Day " + sim.getLoanOverdueDays(game));

        System.out.println(needsOutput);
        System.out.println("\n" + CYAN + "========== SKILLS PROGRESSION ==========" + RESET);
        System.out.println(skillsOutput);

        // Display pets
        if (!sim.getPets().isEmpty()) {
            System.out.println("\n" + CYAN + "========== PETS ==========" + RESET);
            for (simscli.pets.Pet pet : sim.getPets()) {
                System.out.println(pet.getStatusSummary());
            }
        } else {
            System.out.println("\n" + YELLOW + "No pets yet. Visit the Pet Store to get one!" + RESET);
        }

        printDynamicTitle(" ".repeat(width) + "End of Status" + " ".repeat(width));
    }

    // End: Sims
    // Start: Menu
    private void showInitialMenu() {
        boolean initialMenuRunning = true;

        while (initialMenuRunning) {

            List<String> menuOptions = new ArrayList<>();
            menuOptions.add("New Game");

            boolean hasValidSave = SaveGame.hasValidSaveData();
            if (hasValidSave && !isGameReset) {
                menuOptions.add("Continue Game");
            }

            menuOptions.add("Quit Game");

            printDynamicTitle("SIMS GAME - START MENU");

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(BLUE + (i + 1) + ") " + menuOptions.get(i) + RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            switch (selectedOption) {
                case "New Game":
                    if (!confirmStartNewGame()) {
                        System.out.println(YELLOW + "New Game cancelled. Returning to Start Menu." + RESET);
                        break;
                    }

                    game.resetGame();
                    SaveGame.clearSaveFile();
                    isGameReset = true;
                    isGameLoaded = false;

                    System.out.println(GREEN + "New Game started!" + RESET);
                    showSimManagementMenu();
                    initialMenuRunning = false;
                    break;

                case "Continue Game":
                    if (hasValidSave) {
                        if (!isGameLoaded) {
                            SaveGame.loadGame(game);
                            System.out.println(GREEN + "Successfully loaded last game!\n" + RESET);
                        }
                        isGameLoaded = true;
                        showSimManagementMenu();
                        initialMenuRunning = false;
                    }
                    break;
                case "Quit Game":
                    if (!isGameLoaded) {
                        SaveGame.loadGame(game);
                        isGameLoaded = true;
                    }

                    String confirm = in.line("Save current progress before quitting? (y/n): ");

                    if (confirm.equalsIgnoreCase("y")) {
                        SaveGame.saveGame(game);
                        System.out.println("Game saved.");
                    }
                    ExitGuard.markNormalExit();
                    System.out.println(DARK_RED + "Goodbye!" + RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println(RED + "Invalid Option" + RESET);
                    break;
            }
        }
    }

    private void showSimManagementMenu() {
        boolean simMenuRunning = true;

        while (simMenuRunning) {

            List<Sim> sims = game.sims();
            boolean hasSims = !sims.isEmpty();

            printDynamicTitle("SIMS GAME - Sims Management Menu");

            List<String> menuOptions = new ArrayList<>();

            menuOptions.add("Create New Sim");

            if (hasSims) {
                menuOptions.add("Select Existing Sim");
            }

            String activeSimsOption = "";
            if (game.activeSim() != null) {
                activeSimsOption = "Enter [" + game.activeSim().getName() + "] Main Menu (Last Played SIM)";
                if (hasSims) {
                    menuOptions.add(activeSimsOption);
                }
            }

            menuOptions.add("Return to Start Menu");
            menuOptions.add("Quit Game");

            for (int i = 0; i < menuOptions.size(); i++) {
                System.out.println(BLUE + (i + 1) + ") " + menuOptions.get(i) + RESET);
            }

            int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
            int optionIndex = choice - 1;

            String selectedOption = menuOptions.get(optionIndex);

            if (selectedOption == activeSimsOption) {
                selectedOption = "Enter Action Menu";
            }

            switch (selectedOption) {
                case "Select Existing Sim":
                    selectExistingSim();

                    if (game.activeSim() != null) {
                        simMenuRunning = false;   // exit management menu
                    }

                    break;
                case "Create New Sim":
                    createNewSim();
                    break;
                case "Enter Action Menu":
                    if (game.activeSim() != null) {
                        simMenuRunning = false;
                    }
                    break;
                case "Return to Start Menu":
                    showInitialMenu();
                    break;
                case "Quit Game":
                    String confirm = in.line("Save current progress before quitting? (y/n): ");

                    if (confirm.equalsIgnoreCase("y")) {
                        SaveGame.saveGame(game);
                        System.out.println("Game saved.");
                    }
                    ExitGuard.markNormalExit();
                    System.out.println(DARK_RED + "Goodbye!" + RESET);
                    System.exit(0);
                    break;
                default:
                    System.out.println(RED + "Invalid Option!" + RESET);

                    break;
            }
        }
    }

    private void showTravelToLocationMenu() {

        Sim activeSim = game.activeSim();
        printDynamicTitle("SIMS GAME - Location Menu");
        System.out.println("Current Location: " + activeSim.getLocation().name() + "\n");

        List<Location> allLocations = new ArrayList<>(game.location().values());
        List<Location> availableLocations = new ArrayList<>();

        for (Location loc : allLocations) {
            if (loc.key().equalsIgnoreCase("street")) {
                continue;
            }

            if (!loc.key().equals(activeSim.getLocation().key()) && loc.canEnter(activeSim)) {
                availableLocations.add(loc);
            }
        }

        if (availableLocations.isEmpty()) {
            System.out.println(RED + "No other locations available!" + RESET);
            System.out.println(BLUE + "1) Return to " + activeSim.getName() + " Main Menu" + RESET);
            in.intRange("\nChoose: ", 1, 1);
            return;
        }

        for (int i = 0; i < availableLocations.size(); i++) {
            System.out.println(BLUE + (i + 1) + ") " + availableLocations.get(i).name() + RESET);
        }

        int choice = 0;
        // Street dont have action menu
        if (activeSim.getLocation().name() != "Street") {
            System.out.println(BLUE + (availableLocations.size() + 1) + ") View [" + activeSim.getLocation().name() + "] Actions Menu" + RESET);
            System.out.println(BLUE + (availableLocations.size() + 2) + ") Return to [" + activeSim.getName() + "] Main Menu" + RESET);

            choice = in.intRange("\nPlease choose (1-" + (availableLocations.size() + 2) + "): ", 1, availableLocations.size() + 2);
            if (choice == availableLocations.size() + 2) {
                System.out.println(BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + RESET);
                return;
            }

            if (choice == availableLocations.size() + 1) {
                showDoLocationActionsMenu(activeSim.getLocation());
                return;
            }

        } else {
            System.out.println(BLUE + (availableLocations.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + RESET);

            choice = in.intRange("\nPlease choose (1-" + (availableLocations.size() + 1) + "): ", 1, availableLocations.size() + 1);
            if (choice == availableLocations.size() + 1) {
                System.out.println(BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + RESET);
                return;
            }
        }

        Location selectedLoc = availableLocations.get(choice - 1);
        String travelMsg = game.travelTo(selectedLoc.key());
        if (travelMsg.contains("arrived") || travelMsg.contains("entered")) {
            System.out.println(GREEN + travelMsg + RESET);
        } else {
            System.out.println(RED + travelMsg + RESET);
            return;
        }
        showDoLocationActionsMenu(selectedLoc);
    }

    private void showDoLocationActionsMenu(Location location) {

        Sim activeSim = game.activeSim();
        printDynamicTitle("SIMS GAME - [" + location.name() + "] Action Menu");

        List<Action> actions = location.actions(activeSim);
        if (actions.isEmpty()) {
            System.out.println(RED + "No actions available at " + location.name() + RESET);
            return;
        }

        if (location.key().equalsIgnoreCase("bank")) {
            System.out.println("Deposit: $" + activeSim.getBankingSystem().getDeposit()
                    + " | Loan: $" + activeSim.getBankingSystem().getLoanAmount()
                    + "\nSimcoin: $" + activeSim.getSimcoin() + "\n");
        }

        for (int i = 0; i < actions.size(); i++) {
            System.out.println(BLUE + (i + 1) + ") " + actions.get(i).name() + RESET);
        }
        System.out.println(BLUE + (actions.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + RESET);

        int choice = in.intRange("\nPlease choose (1-" + (actions.size() + 1) + "): ", 1, actions.size() + 1);
        if (choice == actions.size() + 1) {
            return;
        }

        int actionIndex = choice - 1;
        String actionMsg = game.performLocationAction(actionIndex);

        if (actionMsg.contains("ate") || actionMsg.contains("showered") || actionMsg.contains("slept") || actionMsg.contains("dined") || actionMsg.contains("played") || actionMsg.contains("read") || actionMsg.contains("socialised") || actionMsg.contains("exercised") || actionMsg.contains("brushed") || actionMsg.contains("used")) {
            System.out.println(GREEN + actionMsg + RESET);
        } else if (actionMsg.contains("can't afford") || actionMsg.contains("need")) {
            System.out.println(RED + actionMsg + RESET);
        } else {
            System.out.println(BLUE + actionMsg + RESET);
        }

        if (!activeSim.isAlive()) {
            System.out.println(RED + activeSim.getName() + " has left the simulation!" + RESET);
        }

    }

    private void showChangeJobMenu() {

        Sim activeSim = game.activeSim();
        printDynamicTitle("SIMS GAME - Change Sim's Job");

        String currentJob = activeSim.getJobName();
        System.out.println("Current Job: " + currentJob + " (Level " + activeSim.getJobLevel() + ")" + "\n");

        List<String> allJobs = Arrays.asList("Chef", "Doctor", "Engineer", "Influencer", "Jobless");
        List<String> availableJobs = new ArrayList<>();

        for (String job : allJobs) {
            if (!job.equals(currentJob)) {
                availableJobs.add(job);
            }
        }

        System.out.println(BLUE + "Available Jobs:" + RESET);
        for (int i = 0; i < availableJobs.size(); i++) {
            String jobName = availableJobs.get(i);
            int jobLevel = activeSim.getAllJobLevels().getOrDefault(jobName, 1);
            System.out.println(BLUE + (i + 1) + ") " + jobName + " (Level " + jobLevel + ")" + RESET);
        }
        System.out.println(BLUE + (availableJobs.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + RESET);

        int choice = in.intRange("\nPlease choose (1-" + (availableJobs.size() + 1) + "): ", 1, availableJobs.size() + 1);
        if (choice == availableJobs.size() + 1) {
            System.out.println(BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + RESET);
            return;
        }

        String selectedJob = availableJobs.get(choice - 1);
        String jobMsg = game.changeJob(selectedJob);

        if (jobMsg.contains("is now a")) {
            System.out.println(GREEN + jobMsg + RESET);
        } else {
            System.out.println(RED + jobMsg + RESET);
        }

    }

    private void showAssetOperationsMenu() {
        Sim activeSim = game.activeSim();

        printDynamicTitle("SIMS GAME - Asset Operations");
        System.out.println("Current Assets: Car = " + (activeSim.getOwnedCar() != null ? "Yes" : "No") + " | House = " + (activeSim.getOwnedHouse() != null ? "Yes" : "No"));
        System.out.println("Simcoin: $" + activeSim.getSimcoin() + " | Loan: $" + activeSim.getLoanAmount() + "\n");

        System.out.println(BLUE + "1) Buy Car ($2000 - 20% down payment: $400)" + RESET);
        System.out.println(BLUE + "2) Buy House ($5000 - 30% down payment: $1500)" + RESET);
        System.out.println(BLUE + "3) Sell Asset" + RESET);
        System.out.println(BLUE + "4) Return to [" + activeSim.getName() + "] Main Menu" + RESET);

        int choice = in.intRange("\nPlease choose (1-4): ", 1, 4);
        switch (choice) {
            case 1:
                if (activeSim.getOwnedCar() != null) {
                    System.out.println(RED + "You already own a Car!" + RESET);
                    break;
                }
                Asset car = new Car(1, "White Audi", 2000, 1.0);
                boolean bought = activeSim.buyAsset(car);
                System.out.println(bought ? GREEN + "Successfully bought " + car.getName() + "!" + RESET : RED + "Failed to buy Car." + RESET);
                break;
            case 2:
                if (activeSim.getOwnedHouse() != null) {
                    System.out.println(RED + "You already own a House!" + RESET);
                    break;
                }
                Asset house = new House(1, "Condominium", 5000);
                boolean boughtHouse = activeSim.buyAsset(house);
                System.out.println(boughtHouse ? GREEN + "Successfully bought " + house.getName() + "!" + RESET : RED + "Failed to buy House." + RESET);
                break;
            case 3:
                showSellAssetMenu(activeSim);
                break;
            case 4:
                System.out.println(BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + RESET);
                return;
        }
    }

    private void showSellAssetMenu(Sim sim) {

        Sim activeSim = game.activeSim();
        printDynamicTitle("SIMS GAME - Sell Asset");

        List<Asset> assets = new ArrayList<>();
        if (sim.getOwnedCar() != null) {
            assets.add(sim.getOwnedCar());
        }
        if (sim.getOwnedHouse() != null) {
            assets.add(sim.getOwnedHouse());
        }

        if (assets.isEmpty()) {
            System.out.println(RED + "No assets to sell!" + RESET);
            return;
        }

        for (int i = 0; i < assets.size(); i++) {
            Asset asset = assets.get(i);
            System.out.println(BLUE + (i + 1) + ") " + asset.getName() + " (Sell Value: $" + asset.sellValue() + ")" + RESET);
        }
        System.out.println(BLUE + (assets.size() + 1) + ") Return to [" + activeSim.getName() + "]  Menu" + RESET);

        int choice = in.intRange("\nPlease choose (1-" + (assets.size() + 1) + "): ", 1, assets.size() + 1);
        if (choice == assets.size() + 1) {
            System.out.println(BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + RESET);
            return;
        }

        Asset toSell = assets.get(choice - 1);
        String result = sim.sellAsset(toSell);
        System.out.println(GREEN + result + RESET);
    }

    /*
    private void showBankingOperationsMenu() {

        Sim activeSim = game.activeSim();

        while (true) {

            
            printDynamicTitle("Banking Menu");

            System.out.println("Deposit: $" + activeSim.getBankingSystem().getDeposit()
                    + " | Loan: $" + activeSim.getBankingSystem().getLoanAmount());
            System.out.println("Simcoin: $" + activeSim.getSimcoin() + "\n");

            System.out.println(BLUE + "1) Deposit Simcoin" + RESET);
            System.out.println(BLUE + "2) Withdraw Simcoin" + RESET);
            System.out.println(BLUE + "3) Apply for Loan" + RESET);
            System.out.println(BLUE + "4) Repay Loan" + RESET);
            System.out.println(BLUE + "5) Return to Main Menu" + RESET);

            int choice = in.intRange("Choose: ", 1, 5);

            switch (choice) {

                case 1:

                    if (activeSim.getSimcoin() <= 0) {
                        System.out.println(RED + "You have no Simcoin to deposit!" + RESET);
                        in.line("Press Enter to continue...");
                        break;
                    }

                    int depositAmt = in.intRange("Deposit amount: $", 1, activeSim.getSimcoin());

                    if (activeSim.getBankingSystem().deposit(depositAmt)) {
                        activeSim.spendSimcoin(depositAmt);
                        System.out.println(GREEN + "Deposited $" + depositAmt + RESET);
                    }

                    in.line("Press Enter to continue...");
                    break;


                case 2:

                    if (activeSim.getBankingSystem().getDeposit() <= 0) {
                        System.out.println(RED + "No money available to withdraw!" + RESET);
                        in.line("Press Enter to continue...");
                        break;
                    }

                    int withdrawAmt = in.intRange("Withdraw amount: $", 1,
                            activeSim.getBankingSystem().getDeposit());

                    if (activeSim.getBankingSystem().withdraw(withdrawAmt)) {
                        activeSim.earnSimcoin(withdrawAmt);
                        System.out.println(GREEN + "Withdrew $" + withdrawAmt + RESET);
                    }

                    in.line("Press Enter to continue...");
                    break;


                case 3:

                    int loanAmt = in.intRange(
                            "Loan amount (max $" + simscli.bank.BankingSystem.getLoanLimit() + "): $",
                            1,
                            simscli.bank.BankingSystem.getLoanLimit()
                                    - activeSim.getBankingSystem().getLoanAmount());

                    if (activeSim.getBankingSystem().applyLoan(loanAmt)) {
                        activeSim.earnSimcoin(loanAmt);
                        System.out.println(GREEN + "Loan approved! $" + loanAmt + RESET);
                    }

                    in.line("Press Enter to continue...");
                    break;


                case 4:

                	if (activeSim.getBankingSystem().getLoanAmount() <= 0) {
                        System.out.println(RED + "No loan to repay." + RESET);
                        in.line("Press Enter to continue...");
                        break;
                    }

                    if (activeSim.getSimcoin() <= 0) {
                        System.out.println(RED + "You do not have enough Simcoin to repay the loan." + RESET);
                        in.line("Press Enter to continue...");
                        break;
                    }

                    int maxRepay = Math.min(activeSim.getSimcoin(), activeSim.getBankingSystem().getLoanAmount());

                    if (maxRepay <= 0) {
                        System.out.println(RED + "You do not have enough Simcoin to repay the loan." + RESET);
                        in.line("Press Enter to continue...");
                        break;
                    }

                    int repayAmt = in.intRange("Repay amount: $", 1, maxRepay);

                    if (activeSim.spendSimcoin(repayAmt)) {
                        activeSim.getBankingSystem().repayLoan(repayAmt);
                        System.out.println(GREEN + "Repaid $" + repayAmt
                                + "! Remaining loan: $" + activeSim.getBankingSystem().getLoanAmount() + RESET);
                    }

                    in.line("Press Enter to continue...");
                    break;


                case 5:
                    return;
            }
        }
    }
     */
    private void showFAQMenu() {

        printDynamicTitle("SIMS GAME - FAQ");
        System.out.println("1. How to earn Simcoin? → Find job and work in the corresponding location (Chef→Restaurant, Doctor→Hospital, etc.)");
        System.out.println("2. How to restore Hygiene? → Use Clean Public (Park) if no house; Brush Teeth/Shower (Home) if house owner");
        System.out.println("3. How to buy Car/House? → Asset Operations → Choose down payment or full payment");
        System.out.println("4. Sleep Rule: 22:00 must be in Park (no house) or Home (house owner) → Else faint");
        System.out.println("5. Travel Cost: Walk (no Car) = Hunger+10/Energy-15; Drive (with Car) = No cost");
        System.out.println("6. Loan Limit: Max $" + simscli.bank.BankingSystem.getLoanLimit());
        System.out.println("7. Interest: Bank deposit earns 0.05% interest per day");
        System.out.println("8. Sim Elimination: Any need hits 0 → Sim leaves the simulation");
        in.line("\n" + BLUE + "Press Enter to return..." + RESET);
    }
    // End: Menu
}
