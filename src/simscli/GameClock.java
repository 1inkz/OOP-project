package simscli;

// GameClock.java
public final class GameClock {
    public static final int MINUTES_PER_DAY = 1440;

    // 1440 in-game minutes in 10 real minutes (600 seconds) -> 2.4 game minutes per real second
    private static final double GAME_MINUTES_PER_REAL_SECOND = 1440.0 / 600.0;

    private int dayNumber;          // starts at 1
    private int minuteOfDay;        // 0..1439 (minutes since midnight)
    private double minuteRemainder; // fractional minutes from real-time ticks
    private int accumulatedMinutes = 0;

    public GameClock() {
        this(1, 0);
    }

    public GameClock(int startDay, int startMinuteOfDay) {
        this.dayNumber = Math.max(1, startDay);
        this.minuteOfDay = clamp(startMinuteOfDay, 0, MINUTES_PER_DAY - 1);
        this.minuteRemainder = 0.0;
    }

    // Time to flow automatically.
    // deltaSeconds is how many real seconds passed since last update.
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
    
    private int onHourPassed(int hoursPassed) {
        return hoursPassed; 
    }

    public int getHoursPassedFromAccumulator() {
        int hours = accumulatedMinutes / 60;
        accumulatedMinutes = accumulatedMinutes % 60;
        return hours;
    }

    // Use this for time spent by actions.
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

    public int getDayNumber() {
        return dayNumber;
    }

    public int getMinuteOfDay() {
        return minuteOfDay;
    }

    public int getHour() {
        return minuteOfDay / 60;
    }

    public int getMinute() {
        return minuteOfDay % 60;
    }

    // Time left in the current day.
    // At 00:00 -> 1440 minutes left. At 23:59 -> 1 minute left.
    public int getMinutesLeftToday() {
        return MINUTES_PER_DAY - minuteOfDay;
    }

    public String getFormattedTime() {
        return String.format(" %02d:%02d", getHour(), getMinute());
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // Midnight (for interest)
    public boolean isMidnight() {
        return minuteOfDay == 0;
    }

    // Reset time to next day 8:00 AM (for faint rule)
    public void resetToNextDayMorning() {
        dayNumber++;
        minuteOfDay = 480; // 8:00 AM
    }
}
