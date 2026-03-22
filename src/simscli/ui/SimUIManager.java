package simscli.ui;

import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

import java.util.List;

/**
 * Manages Sim creation, selection, and Sim-specific UI menus.
 */
public class SimUIManager {
	
	private final Game game;
    private final Input in;
    private final UIHelper uiHelper;
    private final MenuUIManager menuUIManager;
    private boolean tutorialShown = false;

    public SimUIManager(Game game, Input in, UIHelper uiHelper, MenuUIManager menuUIManager) {
    	this.game = game;
        this.in = in;
        this.uiHelper = uiHelper;
        this.menuUIManager = menuUIManager;
    }
    
    /**
     * Displays the three-part game tutorial.
     */
    public void showTutorial() {

    	uiHelper.printDynamicTitle("Tutorial 1/3", uiHelper.DARK_RED);
        System.out.println("1. All actions consume 1 hour of in-game time");
        System.out.println("2. Any need hitting 0 will eliminate your Sim");
        System.out.println("3. Time advances in real-time (1 sec = 1 min)");
        in.line("\nPress Enter to continue...\n");

        uiHelper.printDynamicTitle("Tutorial 2/3", uiHelper.DARK_RED);
        System.out.println("1. Buy a Car to avoid travel costs (Hunger/Energy loss)");
        System.out.println("2. Buy a House to unlock Home actions like sleep and hygiene");
        System.out.println("3. Buy a Hotel to earn passive income at the end of each day");
        in.line("\nPress Enter to continue...\n");

        uiHelper.printDynamicTitle("Tutorial 3/3", uiHelper.DARK_RED);
        System.out.println("1. Bank deposits earn 0.05% interest daily (midnight)");
        System.out.println("2. Max loan limit: $" + simscli.bank.BankingSystem.getLoanLimit());
        System.out.println("3. Dine Out at Restaurant costs $25 and restores Hunger/Social");
        in.line("\nPress Enter to continue...\n");
    }
    
    /**
     * Prompts user to create a new Sim with type selection.
     */
    public void createNewSim() {

    	uiHelper.printDynamicTitle("SIMS GAME - Create New Sims", uiHelper.DARK_RED);

        String name = in.line("Enter Sims name (or type 'cancel' to return): ");
        if (name.equalsIgnoreCase("cancel")) {
            System.out.println(uiHelper.BLUE + "Returning to Sims Management Menu" + uiHelper.RESET);
            return;
        }

        if (name.trim().isEmpty()) {
            System.out.println(uiHelper.RED + "Name cannot be empty!" + uiHelper.RESET);

            createNewSim();
            return;
        }

        System.out.println(uiHelper.BLUE + "\nSelect Sim Type:" + uiHelper.RESET);
        System.out.println(uiHelper.BLUE + "1) Child" + uiHelper.RESET);
        System.out.println(uiHelper.BLUE + "2) Adult" + uiHelper.RESET);
        System.out.println(uiHelper.BLUE + "3) Elder" + uiHelper.RESET);
        int t = in.intRange("Choose: ", 1, 3);
        SimType type = (t == 1) ? SimType.CHILD : (t == 2) ? SimType.ADULT : SimType.ELDER;

        game.createSim(name, type);
        game.markGameModified();
        // Show tutorial once when new sims created
        if (!tutorialShown) {
            showTutorial();
            tutorialShown = true;
        }

        List<Sim> sims = game.sims();
        for (int i = 0; i < sims.size(); i++) {
            Sim sim = sims.get(i);
            if (sim.getName().equals(name)) {
                game.setActiveSim(i);
            }
        }

        System.out.println(uiHelper.GREEN + "Created new Sim: " + name + " (" + type + ")" + uiHelper.RESET);
    }
    
    /**
     * Shows list of existing Sims for the user to select.
     */
    public void selectExistingSim() {

        List<Sim> sims = game.sims();
        uiHelper.printDynamicTitle("SIMS GAME - Select Existing Sims", uiHelper.DARK_RED);

        game.cleanupDeadSims();
        sims = game.sims();

        for (int i = 0; i < sims.size(); i++) {
            Sim sim = sims.get(i);
            String status = sim.isAlive() ? uiHelper.GREEN + "Alive" + uiHelper.RESET : uiHelper.RED + "Gone" + uiHelper.RESET;
            System.out.println(uiHelper.BLUE + (i + 1) + ") " + sim.getName() + " (" + sim.getType() + ") - " + status + uiHelper.RESET);
        }

        System.out.println(uiHelper.BLUE + (sims.size() + 1) + ") Return to Sims Management Menu" + uiHelper.RESET);

        int choice = in.intRange("\nChoose Sims (1-" + (sims.size() + 1) + "): ", 1, sims.size() + 1);
        if (choice == sims.size() + 1) {
        	menuUIManager.showSimManagementMenu();
            return;
        }

        int simIndex = choice - 1;
        game.setActiveSim(simIndex);
        game.markGameModified();
        System.out.println(uiHelper.GREEN + "Active Sims changed to: " + game.activeSim().getName() + uiHelper.RESET);
    }
    
    public void printSimStatus(Sim sim) {

        String needsOutput = "Hunger: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.HUNGER)) + " | "
                + "Energy: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.ENERGY)) + " | "
                + "Hygiene: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.HYGIENE)) + " | "
                + "Social: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.SOCIAL)) + " | "
                + "Fun: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.FUN)) + " | "
                + "Bladder: " + uiHelper.getNeedColor(sim.getNeeds().get(NeedType.BLADDER)) + "\n";

        String skillsOutput
                = "Cooking: " + sim.getSkillLevel(SkillType.COOKING) + " | "
                + "Cleaning: " + sim.getSkillLevel(SkillType.CLEANING) + " | "
                + "Charisma: " + sim.getSkillLevel(SkillType.CHARISMA) + " | "
                + "Fitness: " + sim.getSkillLevel(SkillType.FITNESS) + "\n"
                + "Intelligence: " + sim.getSkillLevel(SkillType.INTELLIGENCE) + " | "
                + "Creativity: " + sim.getSkillLevel(SkillType.CREATIVITY) + " | "
                + "Gaming: " + sim.getSkillLevel(SkillType.GAMING) + " | "
                + "Work Ethic: " + sim.getSkillLevel(SkillType.WORK_ETHIC) + "\n";

        int needsWidth = (needsOutput.length()) / 4;
        int skillsWidth = (skillsOutput.length()) / 6;

        uiHelper.printDynamicTitle(" ".repeat(needsWidth) + sim.getName() + " - Status" + " ".repeat(needsWidth), uiHelper.DARK_RED);

        System.out.println("Name: " + sim.getName() + " | Type: " + sim.getType());
        System.out.println("Job: " + sim.getJobName() + " (Level " + sim.getJobLevel() + ")");
        System.out.println("Location: " + sim.getLocation().name());
        System.out.println("Simcoin: $" + sim.getSimcoin() + " | Bank Savings: $" + sim.getBankingSystem().getDeposit() + " | Loan: $" + sim.getBankingSystem().getLoanAmount());
        System.out.println("Assets: Car = " + (sim.getOwnedCar() != null ? "Yes" : "No")
                + " | House = " + (sim.getOwnedHouse() != null ? "Yes" : "No")
                + " | Hotel = " + (sim.getOwnedHotel() != null ? "Yes" : "No")
            + " | Loan overdue days: " + sim.getLoanOverdueDays(game));

        System.out.println(needsOutput);
        
        // Display critical need warnings in RED
        List<String> warnings = uiHelper.getCriticalNeedWarnings(sim);
        if (!warnings.isEmpty()) {
            System.out.println();
            for (String warning : warnings) {
                System.out.println(uiHelper.RED + warning + uiHelper.RESET);
            }
        }
        
        uiHelper.printDynamicTitle(" ".repeat(skillsWidth) + "Skill Progression" + " ".repeat(skillsWidth), uiHelper.CYAN);
        System.out.println(skillsOutput);

        // Display pets
        if (!sim.getPets().isEmpty()) {
        	uiHelper.printDynamicTitle(" ".repeat(needsWidth) + "Pet" + " ".repeat(needsWidth), uiHelper.YELLOW);
            for (simscli.pets.Pet pet : sim.getPets()) {
            	System.out.println(pet.getStatusSummary());
            }
        } else {
        	uiHelper.printDynamicTitle(" ".repeat(16) + "Pet" + " ".repeat(16), uiHelper.YELLOW);
        	System.out.println("No pets yet. Visit the Pet Store to get one!");
        }
        uiHelper.printDynamicTitle(" ".repeat(needsWidth) + "End of Status" + " ".repeat(needsWidth), uiHelper.DARK_RED);
    }

}
