package simscli;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import simscli.game.Game;

public final class ExitGuard {
    private static final AtomicBoolean normalExit = new AtomicBoolean(false);
    private static final AtomicBoolean hookInstalled = new AtomicBoolean(false);

    private ExitGuard() {}

    public static void install(Game game) {
        if (!hookInstalled.compareAndSet(false, true)) {
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (normalExit.get()) {
                return;
            }

            try {
                System.out.println("\n[System] The game is closing unexpectedly.");
                System.out.print("Save current progress before exit? (y/n): ");
                try (Scanner scanner = new Scanner(System.in)) {
                    String answer = scanner.nextLine().trim();
                    if (answer.equalsIgnoreCase("y")) {
                        SaveGame.saveGame(game);
                        System.out.println("Game saved.");
                    }
                }
            } catch (Exception e) {
                try {
                    SaveGame.saveGame(game);
                    System.out.println("\n[System] Exit input unavailable. Progress was auto-saved.");
                } catch (Exception ignored) {
                    System.out.println("\n[System] Could not save before exit.");
                }
            } finally {
                game.shutdown();
            }
        }, "simscli-exit-guard"));
    }

    public static void markNormalExit() {
        normalExit.set(true);
    }
}
