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
}