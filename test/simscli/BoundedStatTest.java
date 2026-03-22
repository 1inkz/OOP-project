package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import simscli.stats.BoundedStat;

/**
 * Test: Verifies BoundedStat clamping behavior.
 */
public class BoundedStatTest {

    /**
     * Tests that stat cannot go below 0.
     */
    @Test
    public void clampsLowToZero() {
        BoundedStat s = new BoundedStat(10);
        s.add(-999);
        assertEquals(0, s.get());
    }

    /**
     * Tests that stat cannot go above 100.
     */
    @Test
    public void clampsHighToHundred() {
        BoundedStat s = new BoundedStat(90);
        s.add(999);
        assertEquals(100, s.get());
    }
}