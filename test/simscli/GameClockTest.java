package simscli;

/**
 * Test: Verifies GameClock time management and real-time synchronization.
 */
public class GameClockTest {
    
    public static void testDefaultInitialization() {
        GameClock clock = new GameClock();
        assert clock.getDayNumber() == 1 : "Should start at day 1";
        assert clock.getMinuteOfDay() == 0 : "Should start at midnight";
        System.out.println("✓ testDefaultInitialization");
    }

    public static void testCustomTime() {
        GameClock clock = new GameClock(5, 720);
        assert clock.getDayNumber() == 5;
        assert clock.getMinuteOfDay() == 720;
        System.out.println("✓ testCustomTime");
    }

    public static void testGetHour() {
        GameClock clock = new GameClock(1, 480); // 8:00 AM
        assert clock.getHour() == 8;
        System.out.println("✓ testGetHour");
    }

    public static void testGetMinute() {
        GameClock clock = new GameClock(1, 485); // 8:05 AM
        assert clock.getMinute() == 5;
        System.out.println("✓ testGetMinute");
    }

    public static void testSpendMinutesWithinDay() {
        GameClock clock = new GameClock(1, 100);
        clock.spendMinutes(60);
        assert clock.getDayNumber() == 1;
        assert clock.getMinuteOfDay() == 160;
        System.out.println("✓ testSpendMinutesWithinDay");
    }

    public static void testSpendMinutesAdvancesDay() {
        GameClock clock = new GameClock(1, 1400);
        clock.spendMinutes(100);
        assert clock.getDayNumber() == 2;
        assert clock.getMinuteOfDay() == 60;
        System.out.println("✓ testSpendMinutesAdvancesDay");
    }

    public static void testGetMinutesLeftToday() {
        GameClock clock = new GameClock(1, 100);
        assert clock.getMinutesLeftToday() == 1340;
        System.out.println("✓ testGetMinutesLeftToday");
    }

    public static void testResetToNextDayMorning() {
        GameClock clock = new GameClock(3, 1000);
        clock.resetToNextDayMorning();
        assert clock.getDayNumber() == 4;
        assert clock.getMinuteOfDay() == 480; // 8:00 AM
        System.out.println("✓ testResetToNextDayMorning");
    }

    public static void testResetNewGame() {
        GameClock clock = new GameClock(10, 1200);
        clock.resetNewGame();
        assert clock.getDayNumber() == 1;
        assert clock.getMinuteOfDay() == 480;
        System.out.println("✓ testResetNewGame");
    }

    public static void main(String[] args) {
        testDefaultInitialization();
        testCustomTime();
        testGetHour();
        testGetMinute();
        testSpendMinutesWithinDay();
        testSpendMinutesAdvancesDay();
        testGetMinutesLeftToday();
        testResetToNextDayMorning();
        testResetNewGame();
    }
}
