package simscli;

import simscli.stats.BoundedStat;

/**
 * Test: Verifies BoundedStat clamping behavior.
 */
public class BoundedStatTest {

    /**
     * Tests that stat cannot go below 0.
     */
    public static void clampsLowToZero() {
        BoundedStat s = new BoundedStat(10);
        s.add(-999);
        assert s.get() == 0;
        System.out.println("✓ clampsLowToZero");
    }

    /**
     * Tests that stat cannot go above 100.
     */
    public static void clampsHighToHundred() {
        BoundedStat s = new BoundedStat(90);
        s.add(999);
        assert s.get() == 100;
        System.out.println("✓ clampsHighToHundred");
    }

    public static void main(String[] args) {
        clampsLowToZero();
        clampsHighToHundred();
    }
}