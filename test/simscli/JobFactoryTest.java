package simscli;

import simscli.jobs.JobFactory;

public class JobFactoryTest {

    public static void createsKnownJob() {
        assert JobFactory.create("Chef").name().equals("Chef");
        System.out.println("✓ createsKnownJob");
    }

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