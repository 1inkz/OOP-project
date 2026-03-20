package simscli;

import simscli.stats.NeedType;

/**
 * Manages in-game time with real-time synchronization.
 * 
 * Converts real-time (in seconds) to game-time (in minutes) at a fixed rate:
 * 1440 game minutes per 10 real-time seconds (2.4 game minutes per real second).
 * Each day has 1440 minutes (00:00 to 23:59).
 * 
 * Key Features:
 * - Tracks current day number and minute-of-day independently
 * - Handles fractional minute remainders for smooth time progression
 * - Accumulates minutes to detect hourly milestones
 * - Supports time advancement via real-time deltas or direct minute spending
 * 
 * Time flows from minuteOfDay 0 (midnight) to 1439 (23:59), then wraps to next day.
 */
public final class GameClock {
    public static final int MINUTES_PER_DAY = 1440;

    // 1440 in-game minutes in 10 real minutes (600 seconds) -> 2.4 game minutes per real second
    private static final double GAME_MINUTES_PER_REAL_SECOND = 1440.0 / 600.0;

    private int dayNumber;          // starts at 1
    private int minuteOfDay;        // 0..1439 (minutes since midnight)
    private double minuteRemainder; // fractional minutes from real-time ticks
    private int accumulatedMinutes = 0;

    /**
     * Creates a GameClock starting at Day 1, 00:00 (midnight).
     */
    public GameClock() {
        this(1, 0);
    }

    /**
     * Creates a GameClock at a specific day and time.
     * 
     * @param startDay the starting day number (minimum 1)
     * @param startMinuteOfDay the starting minute of day (0-1439, clamped to valid range)
     */
    public GameClock(int startDay, int startMinuteOfDay) {
        this.dayNumber = Math.max(1, startDay);
        this.minuteOfDay = clamp(startMinuteOfDay, 0, MINUTES_PER_DAY - 1);
        this.minuteRemainder = 0.0;
    }

    /**
     * Advances game time based on elapsed real-world seconds.
     * 
     * Accumulates fractional minutes across multiple calls and converts whole minutes
     * into in-game time. Detects hourly milestones and triggers onHourPassed callback.
     * 
     * @param deltaSeconds the number of real-world seconds elapsed since last call
     *                     (non-positive values are ignored)
     */
    public void advanceByRealTime(double deltaSeconds) {
        if (deltaSeconds <= 0) return;

        double gameMinutesToAdd = deltaSeconds * GAME_MINUTES_PER_REAL_SECOND;
        minuteRemainder += gameMinutesToAdd;

        int wholeMinutes = (int) Math.floor(minuteRemainder);
        if (wholeMinutes > 0) {
            minuteRemainder -= wholeMinutes;
            spendMinutes(wholeMinutes);
            
            accumulatedMinutes += wholeMinutes;
            if (accumulatedMinutes >= 60) {
                int hoursPassed = accumulatedMinutes / 60;
                accumulatedMinutes = accumulatedMinutes % 60;
                onHourPassed(hoursPassed);
            }
        }
    }
    
    /**
     * Internal callback triggered when accumulated minutes reach an hour threshold.
     * 
     * @param hoursPassed the number of complete hours that have passed
     * @return the number of hours passed (for potential future use)
     */
    private int onHourPassed(int hoursPassed) {
        return hoursPassed; 
    }

    /**
     * Retrieves and clears accumulated hours from the minute accumulator.
     * 
     * Resets the accumulator after extracting the hour count, allowing
     * external systems to know when a full hour has elapsed.
     * 
     * @return the number of complete hours accumulated
     */
    public int getHoursPassedFromAccumulator() {
        int hours = accumulatedMinutes / 60;
        accumulatedMinutes = accumulatedMinutes % 60;
        return hours;
    }

    /**
     * Directly spends (advances) game time by a specified number of minutes.
     * 
     * Used when actions consume time. Properly handles day boundary transitions,
     * advancing day number and wrapping minute-of-day as needed.
     * 
     * @param minutes the number of game minutes to advance (non-positive values ignored)
     */
    public void spendMinutes(int minutes) {
        if (minutes <= 0) return;

        int total = minuteOfDay + minutes;

        int daysPassed = Math.floorDiv(total, MINUTES_PER_DAY);
        int newMinuteOfDay = Math.floorMod(total, MINUTES_PER_DAY);

        if (daysPassed > 0) {
            dayNumber += daysPassed;
        }
        minuteOfDay = newMinuteOfDay;
    }

    /**
     * Gets the current day number.
     * 
     * @return current day (starts at 1, increases each midnight)
     */
    public int getDayNumber() {
        return dayNumber;
    }

    /**
     * Gets the current minute within the active day (0-1439).
     * 
     * @return current minute of day (0 = midnight, 1439 = 23:59)
     */
    public int getMinuteOfDay() {
        return minuteOfDay;
    }

    /**
     * Gets the current hour of day (0-23).
     * 
     * @return hour extracted from minute-of-day
     */
    public int getHour() {
        return minuteOfDay / 60;
    }

    /**
     * Gets the current minute within the active hour (0-59).
     * 
     * @return minute remainder after extracting the hour
     */
    public int getMinute() {
        return minuteOfDay % 60;
    }

    /**
     * Calculates remaining minutes until end of current day (midnight).
     * 
     * At 00:00 (midnight) returns 1440; at 23:59 returns 1.
     * Useful for scheduling events that must occur before day end.
     * 
     * @return minutes remaining in current day
     */
    public int getMinutesLeftToday() {
        return MINUTES_PER_DAY - minuteOfDay;
    }

    /**
     * Returns a formatted time string in HH:MM format.
     * 
     * @return formatted time (e.g., " 14:30" with leading space)
     */
    public String getFormattedTime() {
        return String.format(" %02d:%02d", getHour(), getMinute());
    }

    /**
     * Utility method to constrain a value within a min/max range.
     * 
     * @param value the value to constrain
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return value clamped to [min, max]
     */
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Always start a day at 8:00 AM.
     * 
     * Used when a Sim faints and must spend the remainder of the day unconscious.
     * Clears remaining game time and starts fresh at 08:00 (480 minutes into day).
     */
    public void resetToNextDayMorning() {
        dayNumber++;
        minuteOfDay = 480; // 8:00 AM
    }
    
    /**
     * Resets the game clock to the initial state for a new game.
     * 
     * Sets clock to Day 1, 08:00 AM (480 minutes).
     * Clears all accumulated time remnants.
     */
    public void resetNewGame() {
        dayNumber = 1;
        minuteOfDay = 480; // 8:00 AM
    }
}
