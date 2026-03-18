package simscli;

import simscli.stats.Needs;
import simscli.stats.NeedType;

/**
 * Test: Verifies Needs tracking and critical state detection.
 */
public class NeedsTest {
    
    public static void testSetNeed() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        assert needs.get(NeedType.HUNGER) == 50;
        System.out.println("✓ testSetNeed");
    }

    public static void testSetNeedClampsToZero() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, -100);
        assert needs.get(NeedType.HUNGER) == 0;
        System.out.println("✓ testSetNeedClampsToZero");
    }

    public static void testSetNeedClampsTo100() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 150);
        assert needs.get(NeedType.HUNGER) == 100;
        System.out.println("✓ testSetNeedClampsTo100");
    }

    public static void testAddNeedPositive() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        needs.add(NeedType.HUNGER, 20);
        assert needs.get(NeedType.HUNGER) == 70;
        System.out.println("✓ testAddNeedPositive");
    }

    public static void testAddNeedNegative() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 50);
        needs.add(NeedType.HUNGER, -30);
        assert needs.get(NeedType.HUNGER) == 20;
        System.out.println("✓ testAddNeedNegative");
    }

    public static void testIsCriticalTrue() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 10);
        assert needs.isCritical(NeedType.HUNGER) : "Should be critical at 10";
        System.out.println("✓ testIsCriticalTrue");
    }

    public static void testIsCriticalFalse() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 20);
        assert !needs.isCritical(NeedType.HUNGER) : "Should not be critical at 20";
        System.out.println("✓ testIsCriticalFalse");
    }

    public static void testCriticalAtZero() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 0);
        assert needs.isCritical(NeedType.HUNGER) : "Must be critical at 0";
        System.out.println("✓ testCriticalAtZero");
    }

    public static void testMultipleNeeds() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 30);
        needs.set(NeedType.ENERGY, 60);
        needs.set(NeedType.SOCIAL, 10);
        
        assert needs.get(NeedType.HUNGER) == 30;
        assert needs.get(NeedType.ENERGY) == 60;
        assert needs.get(NeedType.SOCIAL) == 10;
        System.out.println("✓ testMultipleNeeds");
    }

    public static void testAllNeedTypes() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 11);
        needs.set(NeedType.ENERGY, 22);
        needs.set(NeedType.HYGIENE, 33);
        needs.set(NeedType.SOCIAL, 44);
        needs.set(NeedType.FUN, 55);
        needs.set(NeedType.BLADDER, 66);
        
        assert needs.get(NeedType.HUNGER) == 11;
        assert needs.get(NeedType.ENERGY) == 22;
        assert needs.get(NeedType.HYGIENE) == 33;
        assert needs.get(NeedType.SOCIAL) == 44;
        assert needs.get(NeedType.FUN) == 55;
        assert needs.get(NeedType.BLADDER) == 66;
        System.out.println("✓ testAllNeedTypes");
    }

    public static void testSequentialAdditions() {
        Needs needs = new Needs();
        needs.set(NeedType.HUNGER, 40);
        needs.add(NeedType.HUNGER, 10);
        needs.add(NeedType.HUNGER, 15);
        needs.add(NeedType.HUNGER, -20);
        
        assert needs.get(NeedType.HUNGER) == 45;
        System.out.println("✓ testSequentialAdditions");
    }

    public static void main(String[] args) {
        testSetNeed();
        testSetNeedClampsToZero();
        testSetNeedClampsTo100();
        testAddNeedPositive();
        testAddNeedNegative();
        testIsCriticalTrue();
        testIsCriticalFalse();
        testCriticalAtZero();
        testMultipleNeeds();
        testAllNeedTypes();
        testSequentialAdditions();
    }
}
