package simscli;

import simscli.stats.BoundedStat;

public class BoundedStatTest {

    public static void clampsLowToZero() {
        BoundedStat s = new BoundedStat(10);
        s.add(-999);
        assert s.get() == 0;
        System.out.println("✓ clampsLowToZero");
    }

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