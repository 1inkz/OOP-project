package simscli.ui;

import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.Hotel;
import simscli.asset.House;
import simscli.game.Game;
import simscli.location.Location;
import simscli.sims.Sim;
import simscli.actions.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages location travel, asset operations, and location action menus.
 */
public class BusinessUIManager {
	
    private Game game;
    private final Input in;
    private final UIHelper uiHelper;

    public BusinessUIManager(Game game, Input in, UIHelper uiHelper) {
        this.game = game;
        this.in = in;
        this.uiHelper = uiHelper;
    }

    /**
     * Displays available locations for travel.
     */
    public void showTravelToLocationMenu() {
    	Sim activeSim = game.activeSim();
        uiHelper.printDynamicTitle("SIMS GAME - Location Menu", uiHelper.DARK_RED);
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
            System.out.println(uiHelper.RED + "No other locations available!" + uiHelper.RESET);
            System.out.println(uiHelper.BLUE + "1) Return to " + activeSim.getName() + " Main Menu" + uiHelper.RESET);
            in.intRange("\nChoose: ", 1, 1);
            return;
        }

        for (int i = 0; i < availableLocations.size(); i++) {
            System.out.println(uiHelper.BLUE + (i + 1) + ") " + availableLocations.get(i).name() + uiHelper.RESET);
        }

        int choice = 0;
        // Street dont have action menu
        if (!"Street".equals(activeSim.getLocation().name())) {
            System.out.println(uiHelper.BLUE + (availableLocations.size() + 1) + ") View [" + activeSim.getLocation().name() + "] Actions Menu" + uiHelper.RESET);
            System.out.println(uiHelper.BLUE + (availableLocations.size() + 2) + ") Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);

            choice = in.intRange("\nPlease choose (1-" + (availableLocations.size() + 2) + "): ", 1, availableLocations.size() + 2);
            if (choice == availableLocations.size() + 2) {
                System.out.println(uiHelper.BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);
                return;
            }

            if (choice == availableLocations.size() + 1) {
                showDoLocationActionsMenu(activeSim.getLocation());
                return;
            }

        } else {
            System.out.println(uiHelper.BLUE + (availableLocations.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);

            choice = in.intRange("\nPlease choose (1-" + (availableLocations.size() + 1) + "): ", 1, availableLocations.size() + 1);
            if (choice == availableLocations.size() + 1) {
                System.out.println(uiHelper.BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);
                return;
            }
        }

        Location selectedLoc = availableLocations.get(choice - 1);
        String travelMsg = game.travelTo(selectedLoc.key());

        // Determine success from actual location state, not message wording.
        boolean travelSucceeded = activeSim.getLocation() != null
                && selectedLoc.key().equalsIgnoreCase(activeSim.getLocation().key());

        if (travelSucceeded) {
            System.out.println(uiHelper.GREEN + travelMsg + uiHelper.RESET);
        } else {
            System.out.println(uiHelper.RED + travelMsg + uiHelper.RESET);
            return;
        }
        // Immediately open the destination's action menu after successful travel.
        showDoLocationActionsMenu(activeSim.getLocation());
    }
    
    public void showDoLocationActionsMenu(Location location) {
    	Sim activeSim = game.activeSim();
    	uiHelper.printDynamicTitle("SIMS GAME - [" + location.name() + "] Action Menu", uiHelper.DARK_RED);

        List<Action> actions = location.actions(activeSim);
        if (actions.isEmpty()) {
            System.out.println(uiHelper.RED + "No actions available at " + location.name() + uiHelper.RESET);
            return;
        }

        if (location.key().equalsIgnoreCase("bank")) {
            System.out.println("Deposit: $" + activeSim.getBankingSystem().getDeposit()
                    + " | Loan: $" + activeSim.getBankingSystem().getLoanAmount()
                    + "\nSimcoin: $" + activeSim.getSimcoin() + "\n");
        }

        for (int i = 0; i < actions.size(); i++) {
            System.out.println(uiHelper.BLUE + (i + 1) + ") " + actions.get(i).name() + uiHelper.RESET);
        }
        System.out.println(uiHelper.BLUE + (actions.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);

        int choice = in.intRange("\nPlease choose (1-" + (actions.size() + 1) + "): ", 1, actions.size() + 1);
        if (choice == actions.size() + 1) {
            return;
        }

        int actionIndex = choice - 1;
        String actionMsg = game.performLocationAction(actionIndex);

        if (actionMsg.contains("ate") || actionMsg.contains("showered") || actionMsg.contains("slept") || actionMsg.contains("dined") || actionMsg.contains("played") || actionMsg.contains("read") || actionMsg.contains("socialised") || actionMsg.contains("exercised") || actionMsg.contains("brushed") || actionMsg.contains("used")) {
            System.out.println(uiHelper.GREEN + actionMsg + uiHelper.RESET);
        } else if (actionMsg.contains("can't afford") || actionMsg.contains("need")) {
            System.out.println(uiHelper.RED + actionMsg + uiHelper.RESET);
        } else {
            System.out.println(uiHelper.BLUE + actionMsg + uiHelper.RESET);
        }
    }
    
    public void showChangeJobMenu() {
    	Sim activeSim = game.activeSim();
        uiHelper.printDynamicTitle("SIMS GAME - [" + activeSim.getName() + "'s] Job", uiHelper.DARK_RED);

        String currentJob = activeSim.getJobName();
        System.out.println("Current Job: " + currentJob + " (Level " + activeSim.getJobLevel() + ")" + "\n");

        List<String> allJobs = Arrays.asList("Cleaner", "Waiter", "Chef", "Doctor", "Jobless");
        List<String> availableJobs = new ArrayList<>();

        for (String job : allJobs) {
            if (!job.equals(currentJob)) {
                availableJobs.add(job);
            }
        }

        System.out.println(uiHelper.BLUE + "Available Jobs:" + uiHelper.RESET);
        for (int i = 0; i < availableJobs.size(); i++) {
            String jobName = availableJobs.get(i);
            int jobLevel = activeSim.getAllJobLevels().getOrDefault(jobName, 1);
            System.out.println(uiHelper.BLUE + (i + 1) + ") " + jobName + " (Level " + jobLevel + ")" + uiHelper.RESET);
        }
        System.out.println(uiHelper.BLUE + (availableJobs.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);

        int choice = in.intRange("\nPlease choose (1-" + (availableJobs.size() + 1) + "): ", 1, availableJobs.size() + 1);
        if (choice == availableJobs.size() + 1) {
            System.out.println(uiHelper.BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);
            return;
        }

        String selectedJob = availableJobs.get(choice - 1);
        String jobMsg = game.changeJob(selectedJob);

        if (jobMsg.contains("is now a")) {
            System.out.println(uiHelper.GREEN + jobMsg + uiHelper.RESET);
        } else {
            System.out.println(uiHelper.RED + jobMsg + uiHelper.RESET);
        }

    }
    
    public void showAssetOperationsMenu() {
    	Sim activeSim = game.activeSim();
        
        List<String> menuOptions = new ArrayList<>();
        
        if (activeSim.getOwnedCar() == null) { menuOptions.add("Buy Car ($2000 - 20% down payment: $400, daily maintenance: $20, lower travel fatigue)"); }
        if (activeSim.getOwnedHouse() == null) { menuOptions.add("Buy House ($4000 - 25% down payment: $1000)"); }
        if (activeSim.getOwnedHotel() == null) { menuOptions.add("Buy Hotel ($7000 - 40% down payment: $2800, variable economy income)"); }
        else { menuOptions.add("Upgrade Hotel"); }
        if (activeSim.getOwnedCar() != null || activeSim.getOwnedHouse() != null || activeSim.getOwnedHotel() != null) { menuOptions.add("Sell Asset"); }
        menuOptions.add("Return to [" + activeSim.getName() + "] Main Menu");
        
        uiHelper.printDynamicTitle("SIMS GAME - Asset Operations", uiHelper.DARK_RED);
        System.out.println("Current Assets: Car = " + (activeSim.getOwnedCar() != null ? "Yes" : "No")
                + " | House = " + (activeSim.getOwnedHouse() != null ? "Yes" : "No")
                + " | Hotel = " + (activeSim.getOwnedHotel() != null ? "Yes" : "No"));
        if (activeSim.getOwnedHotel() != null) {
            System.out.println("Hotel Level: " + activeSim.getOwnedHotelLevel());
        }
        System.out.println("Simcoin: $" + activeSim.getSimcoin() + " | Loan: $" + activeSim.getLoanAmount() + "\n");

        for (int i = 0; i < menuOptions.size(); i++) {
        	System.out.println(uiHelper.BLUE + (i + 1) + ") " + menuOptions.get(i) + uiHelper.RESET);
        }
        
        int choice = in.intRange("\nPlease choose (1-" + menuOptions.size() + "): ", 1, menuOptions.size());
        int optionIndex = choice -1;
        
        String selectedOption = menuOptions.get(optionIndex);
        
        switch (selectedOption) {
            case "Buy Car ($2000 - 20% down payment: $400, daily maintenance: $20, lower travel fatigue)":
                Asset car = new Car(1, "White Audi", 2000, 0.4);
                boolean boughtCar = activeSim.buyAsset(car);
                System.out.println(boughtCar ? uiHelper.GREEN + "Successfully bought " + car.getName() + "!" + uiHelper.RESET : uiHelper.RED + "Failed to buy Car." + uiHelper.RESET);
                break;
            case "Buy House ($4000 - 25% down payment: $1000)":
                Asset house = new House(1, "Condominium", 4000);
                boolean boughtHouse = activeSim.buyAsset(house);
                System.out.println(boughtHouse ? uiHelper.GREEN + "Successfully bought " + house.getName() + "!" + uiHelper.RESET : uiHelper.RED + "Failed to buy House." + uiHelper.RESET);
                break;
            case "Buy Hotel ($7000 - 40% down payment: $2800, variable economy income)":
                Asset hotel = new Hotel(1, "Intercontinental Hotel", 7000);
                boolean boughtHotel = activeSim.buyAsset(hotel);
                System.out.println(boughtHotel ? uiHelper.GREEN + "Successfully bought " + hotel.getName() + "!" + uiHelper.RESET : uiHelper.RED + "Failed to buy Hotel." + uiHelper.RESET);
                break;
            case "Upgrade Hotel":
                int upgradeCost = activeSim.getOwnedHotelUpgradeCost();
                if (upgradeCost <= 0) {
                    System.out.println(uiHelper.YELLOW + "Hotel is already at max level." + uiHelper.RESET);
                    break;
                }

                if (activeSim.upgradeOwnedHotel()) {
                    System.out.println(uiHelper.GREEN + "Hotel upgraded to level " + activeSim.getOwnedHotelLevel() + " for $" + upgradeCost + "." + uiHelper.RESET);
                } else {
                    System.out.println(uiHelper.RED + "Not enough Simcoin to upgrade Hotel (need $" + upgradeCost + ")." + uiHelper.RESET);
                }
                break;
            case "Sell Asset":
                showSellAssetMenu();
                break;
            default:
                return;
        }
    }
    
    public void showSellAssetMenu() {
    	Sim activeSim = game.activeSim();
        uiHelper.printDynamicTitle("SIMS GAME - Sell Asset", uiHelper.DARK_RED);

        List<Asset> assets = new ArrayList<>();
        if (activeSim.getOwnedCar() != null) {
            assets.add(activeSim.getOwnedCar());
        }
        if (activeSim.getOwnedHouse() != null) {
            assets.add(activeSim.getOwnedHouse());
        }
        if (activeSim.getOwnedHotel() != null) {
            assets.add(activeSim.getOwnedHotel());
        }

        for (int i = 0; i < assets.size(); i++) {
            Asset asset = assets.get(i);
            System.out.println(uiHelper.BLUE + (i + 1) + ") " + asset.getName() + " (Sell Value: $" + asset.sellValue() + ")" + uiHelper.RESET);
        }
        System.out.println(uiHelper.BLUE + (assets.size() + 1) + ") Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);

        int choice = in.intRange("\nPlease choose (1-" + (assets.size() + 1) + "): ", 1, assets.size() + 1);
        if (choice == assets.size() + 1) {
            System.out.println(uiHelper.BLUE + "Return to [" + activeSim.getName() + "] Main Menu" + uiHelper.RESET);
            return;
        }

        Asset toSell = assets.get(choice - 1);
        String result = activeSim.sellAsset(toSell);
        System.out.println(uiHelper.GREEN + result + uiHelper.RESET);
    }
}
