package simscli;

import org.junit.Test;
import simscli.stats.BoundedStat;

import static org.junit.Assert.*;

public class BoundedStatTest {

    @Test
    public void clampsLowToZero() {
        BoundedStat s = new BoundedStat(10);
        s.add(-999);
        assertEquals(0, s.get());
    }

    @Test
    public void clampsHighToHundred() {
        BoundedStat s = new BoundedStat(90);
        s.add(999);
        assertEquals(100, s.get());
    }
}