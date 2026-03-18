package simscli;

import simscli.jobs.JobFactory;

/**
 * Test: Verifies JobFactory creates correct Jobs.
 */
public class JobFactoryTest {

    /**
     * Tests that Chef job is created correctly.
     */
    public static void createsKnownJob() {
        assert JobFactory.create("Chef").name().equals("Chef");
        System.out.println("✓ createsKnownJob");
    }

    /**
     * Tests that unknown job names throw exception.
     */
    public static void unknownJobThrows() {
        try {
            JobFactory.create("Wizard");
            assert false : "Should have thrown IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ unknownJobThrows");
        }
    }

    public static void main(String[] args) {
        createsKnownJob();
        unknownJobThrows();
    }
}