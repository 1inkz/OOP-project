package ui;

import action.*;
import location.Location;
import sim.Sim;
import world.World;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner sc = new Scanner(System.in);

    public void run(Sim sim, World world){
        System.out.println("=== CLI SIMS ===");

        Location current = world.getLocations().getFirst();

        while (true){
            System.out.println("You are at: " + current.getLocation());

            printSimStatus(sim);
            System.out.println("\nChoose:");
            System.out.println("1) Go to a location");
            System.out.println("2) Do an action here");
            System.out.println("0) Quit");

            int choice = readInt(0, 2);
            if (choice == 0) {
                System.out.println("Bye!");
                return;
            } else if (choice == 1) {
                current = chooseLocation(world);
            } else {
                doActionHere(sim, world, current);
            }

        }
    }

    private void printSimStatus(Sim sim){
        System.out.println("Money: $"+sim.getMoney());
        System.out.println("Bank: $"+sim.getBankbalance().bankBalance
        +"| Loan owned: $"+sim.getBankbalance().loanBalance);
        System.out.println("Needs: $"+sim.getNeeds());
        System.out.println("Traits: "+sim.traitsString());
    }

    private Location chooseLocation(World world){
        System.out.println("\n--- Locations ---");
        List<Location> locs = world.getLocations();
        for (int i = 0; i < locs.size(); i++) {
            System.out.println((i + 1) + ") " + locs.get(i).getLocation());
        }

        int pick = readInt(1, locs.size());
        Location chosen = locs.get(pick - 1);
        System.out.println("Travelled to "+chosen.getLocation());

        return chosen;

    }

    private void doActionHere(Sim sim, World world, Location location){
        List<Action> actions = location.availableActions(sim, world);

        System.out.println("\n--- Actions at " + location.getLocation() + " ---");
        for (int i = 0; i < actions.size(); i++) {
            Action a = actions.get(i);
            System.out.println((i + 1) + ") " + a.name());
        }
        System.out.println("0) Cancel");

        int pick = readInt(0, actions.size());
        if (pick == 0) return;

        Action chosen = actions.get(pick - 1);
        ActionContext ctx = new ActionContext(sim, world, location);

        // Action base requirement
        if (!chosen.canPerform(ctx)) {
            System.out.println("You can't do that right now.");
            return;
        }

        // Trait pre-check
        if (!sim.canDo(chosen)) {
            System.out.println("You can't do that right now (trait blocked it).");
            return;
        }

        // Perform action
        Outcome out = chosen.perform(ctx);

        // Traits can modify outcome
        sim.applyTraitEffects(chosen, out);

        // Print messages returned by the action
        for (String msg : out.messages) {
            System.out.println(msg);
        }

    }

    private int readInt(int min, int max){
        while (true){
            System.out.print("> ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Enter a number from " + min + " to " + max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }
}
