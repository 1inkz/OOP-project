package simscli.ui;

import java.util.Scanner;

/**
 * Handles user input from console.
 */
public final class Input {
    private final Scanner sc = new Scanner(System.in);

    /**
     * Reads a line of text from the user.
     * @param prompt message to display
     * @return trimmed user input
     */
    public String line(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Reads an integer input within the specified range.
     * Prompts again on invalid input.
     * @param prompt message to display
     * @param min minimum acceptable value
     * @param max maximum acceptable value
     * @return validated integer within range
     */
    public int intRange(String prompt, int min, int max) {
        while (true) {
            String s = line(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Enter " + min + " to " + max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }
    
    /**
     * Reads a string input and validates it against two case-insensitive options.
     * @param prompt message to display
     * @param option1 first valid choice
     * @param option2 second valid choice
     * @return the validated string input
     */
    public String stringOptions(String prompt, String option1, String option2) {
        while (true) {
            String s = line(prompt).trim();
            if (s.equalsIgnoreCase(option1) || s.equalsIgnoreCase(option2)) {
                return s;
            }
            System.out.println("Please enter '" + option1 + "' or '" + option2 + "'.");
        }
    }
}