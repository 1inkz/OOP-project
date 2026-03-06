package simscli.ui;

import java.util.Scanner;

public final class Input {
    private final Scanner sc = new Scanner(System.in);

    public String line(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

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