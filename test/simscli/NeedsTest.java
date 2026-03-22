package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.stats.Needs;
import simscli.stats.NeedType;

/**
 * Test: Verifies Needs tracking and critical state detection.
 */
public class NeedsTest {

    @Test
    public void testSetNeed() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        assertEquals(50, needs.get(NeedType.HUNGER));
    }

    @Test
    public void testSetNeedClampsToZero() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, -100);
        assertEquals(0, needs.get(NeedType.HUNGER));
    }

    @Test
    public void testSetNeedClampsTo100() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 150);
        assertEquals(100, needs.get(NeedType.HUNGER));
    }

    @Test
    public void testAddNeedPositive() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        needs.add(NeedType.HUNGER, 20);
        assertEquals(70, needs.get(NeedType.HUNGER));
    }

    @Test
    public void testAddNeedNegative() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        needs.add(NeedType.HUNGER, -30);
        assertEquals(20, needs.get(NeedType.HUNGER));
    }

    @Test
    public void testIsCriticalTrue() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 10);
        assertTrue(needs.isCritical(NeedType.HUNGER));
    }

    @Test
    public void testIsCriticalFalse() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 20);
        assertFalse(needs.isCritical(NeedType.HUNGER));
    }

    @Test
    public void testCriticalAtZero() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 0);
        assertTrue(needs.isCritical(NeedType.HUNGER));
    }

    @Test
    public void testMultipleNeeds() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 30);
        needs.set(NeedType.ENERGY, 60);
        needs.set(NeedType.SOCIAL, 10);

        assertEquals(30, needs.get(NeedType.HUNGER));
        assertEquals(60, needs.get(NeedType.ENERGY));
        assertEquals(10, needs.get(NeedType.SOCIAL));
    }

    @Test
    public void testAllNeedTypes() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 11);
        needs.set(NeedType.ENERGY, 22);
        needs.set(NeedType.HYGIENE, 33);
        needs.set(NeedType.SOCIAL, 44);
        needs.set(NeedType.FUN, 55);
        needs.set(NeedType.BLADDER, 66);
        
        assertEquals(11, needs.get(NeedType.HUNGER));
        assertEquals(22, needs.get(NeedType.ENERGY));
        assertEquals(33, needs.get(NeedType.HYGIENE));
        assertEquals(44, needs.get(NeedType.SOCIAL));
        assertEquals(55, needs.get(NeedType.FUN));
        assertEquals(66, needs.get(NeedType.BLADDER));
    }

    @Test
    public void testSequentialAdditions() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 40);
        needs.add(NeedType.HUNGER, 10);
        needs.add(NeedType.HUNGER, 15);
        needs.add(NeedType.HUNGER, -20);

        assertEquals(45, needs.get(NeedType.HUNGER));
    }
}
