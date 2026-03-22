package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simscli.game.GameClock;

/**
 * Test: Verifies GameClock time management and real-time synchronization.
 */
public class GameClockTest {
    
    @Test
    public void testDefaultInitialization() {
        GameClock clock = new GameClock();
        assertEquals(1, clock.getDayNumber());
        assertEquals(0, clock.getMinuteOfDay());
    }

    @Test
    public void testCustomTime() {
        GameClock clock = new GameClock(5, 720);
        assertEquals(5, clock.getDayNumber());
        assertEquals(720, clock.getMinuteOfDay());
    }

    @Test
    public void testGetHour() {
        GameClock clock = new GameClock(1, 480); // 8:00 AM
        assertEquals(8, clock.getHour());
    }

    @Test
    public void testGetMinute() {
        GameClock clock = new GameClock(1, 485); // 8:05 AM
        assertEquals(5, clock.getMinute());
    }

    @Test
    public void testSpendMinutesWithinDay() {
        GameClock clock = new GameClock(1, 100);
        clock.spendMinutes(60);
        assertEquals(1, clock.getDayNumber());
        assertEquals(160, clock.getMinuteOfDay());
    }

    @Test
    public void testSpendMinutesAdvancesDay() {
        GameClock clock = new GameClock(1, 1400);
        clock.spendMinutes(100);
        assertEquals(2, clock.getDayNumber());
        assertEquals(60, clock.getMinuteOfDay());
    }

    @Test
    public void testGetMinutesLeftToday() {
        GameClock clock = new GameClock(1, 100);
        assertEquals(1340, clock.getMinutesLeftToday());
    }

    @Test
    public void testResetToNextDayMorning() {
        GameClock clock = new GameClock(3, 1000);
        clock.resetToNextDayMorning();
        assertEquals(4, clock.getDayNumber());
        assertEquals(480, clock.getMinuteOfDay());
    }

    @Test
    public void testResetNewGame() {
        GameClock clock = new GameClock(10, 1200);
        clock.resetNewGame();
        assertEquals(1, clock.getDayNumber());
        assertEquals(480, clock.getMinuteOfDay());
    }
}
