package simscli.ui;

import simscli.actions.*;
import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;

public final class ConsoleUI {
    private final Game game;
    private final Input in = new Input();

    public ConsoleUI(Game game) {
        this.game = game;
    }

    public void run() {
        System.out.println("=== SIMS CLI (No Events Edition) ===");
        System.out.println("Rule: if ANY need hits 0, the Sim leaves the simulation.\n");

        boolean running = true;
        while (running) {
            System.out.println("\n[" + game.timeString() + "]");
            showActive();

            System.out.println("\nMenu:");
            System.out.println("1) Create Sim");
            System.out.println("2) Switch Active Sim");
            System.out.println("3) View Household");
            System.out.println("4) Travel to Location");
            System.out.println("5) Do Location Action");
            System.out.println("6) Change Job");
            System.out.println("7) Pass Time (1 hour)");
            System.out.println("0) Quit");

            int c = in.intRange("Choose: ", 0, 7);
            switch (c) {
                case 1: createSim(); break;
                case 2: switchSim(); break;
                case 3: listSims(); break;
                case 4: travel(); break;
                case 5: doLocationAction(); break;
                case 6: changeJob(); break;
                case 7: game.tickOneHour(); System.out.println("Time passes..."); break;
                case 0: running = false; break;
                default: break;
            }

            if (game.sims().isEmpty()) {
                System.out.println("\nAll Sims are gone. Game over.");
                running = false;
            }
        }

        // important: stop thread pool (prevents memory/thread leak)
        game.shutdown();
        System.out.println("Bye!");
    }

    private void showActive() {
        Sim s = game.activeSim();

        if (s == null) {
            System.out.println("Active: (none)");
            return;
        }
        System.out.println("Location: " + s.getLocation().name() + "  (Use option 5 for actions here)");

        System.out.println("Active: " + s.summaryLine() + " | Mood=" + s.moodScore());

        if (s.getLocation() != null) {
            System.out.println("Location: " + s.getLocation().name());
        } else {
            System.out.println("Location: (none)");
        }
}

    private void createSim() {
        String name = in.line("Name: ");
        System.out.println("Type: 1) Child 2) Adult 3) Elder");
        int t = in.intRange("Choose: ", 1, 3);
        SimType type = (t == 1) ? SimType.CHILD : (t == 2) ? SimType.ADULT : SimType.ELDER;

        game.createSim(name, type);
        System.out.println("Created " + type + ": " + name);
    }

    private void switchSim() {
        if (game.sims().isEmpty()) return;
        listSims();
        int idx = in.intRange("Index: ", 0, game.sims().size() - 1);
        game.setActiveSim(idx);
        System.out.println("Active Sim changed.");
    }

    private void listSims() {
        System.out.println("\n--- Household ---");
        for (int i = 0; i < game.sims().size(); i++) {
            System.out.println(i + ") " + game.sims().get(i).summaryLine());
        }
        System.out.println("---------------");
    } 


    private void useObject() {
        String key = in.line("Object key: ");
        System.out.println(game.useObject(key));
    }

    private void doAction() {
        if (game.activeSim() == null) 
        {
            System.out.println("Create a Sim first.");
            return;
        }
        
        String a = in.line("Action (snack/nap/brush/socialise/work): ").toLowerCase();
        Action action;
        switch (a) {
            case "snack": action = ActionFactory.create(ActionType.EAT_SNACK); break;
            case "nap": action = ActionFactory.create(ActionType.NAP); break;
            case "brush": action = ActionFactory.create(ActionType.BRUSH_TEETH); break;
            case "socialise": action = ActionFactory.create(ActionType.SOCIALISE); break;
            case "work": action = ActionFactory.create(ActionType.WORK); break;
            default:
                System.out.println("Unknown action.");
                return;
        }
        System.out.println(game.performAction(action));
    }

    private void changeJob() {
        if (game.activeSim() == null) {
            System.out.println("Create a Sim first.");
            return;
        }

        System.out.println("Jobs available: Chef / Doctor / Engineer / Influencer");
        String job = in.line("Choose job: ");
        System.out.println(game.changeJob(job));
    }

    private void travel() {
        if (game.activeSim() == null) 
        {
            System.out.println("Create a Sim first.");
            return;
        }

        // Build a stable ordered list for indexing
        var locList = new java.util.ArrayList<>(game.locations().values());

        System.out.println("\nWhere do you want to go?");
        for (int i = 0; i < locList.size(); i++) {
            System.out.println(i + ") " + locList.get(i).name() + "  [" + locList.get(i).key() + "]");
        }

        int idx = in.intRange("Choose location number: ", 0, locList.size() - 1);
        String key = locList.get(idx).key();

        System.out.println(game.travelTo(key));
    }

    private void doLocationAction() {
        Sim s = game.activeSim();
        if (s == null) {
            System.out.println("Create a Sim first.");
            return;
        }
        if (s.getLocation() == null) {
            System.out.println("This Sim has no location. Travel first.");
            return;
        }

        var actions = s.getLocation().actions();
        if (actions.isEmpty()) {
            System.out.println("No actions available at " + s.getLocation().name() + ".");
            return;
        }

        System.out.println("\nActions at " + s.getLocation().name() + ":");
        for (int i = 0; i < actions.size(); i++) {
            System.out.println(i + ") " + actions.get(i).name());
        }

        int idx = in.intRange("Choose action number: ", 0, actions.size() - 1);
        System.out.println(game.performLocationAction(idx));
    }
}