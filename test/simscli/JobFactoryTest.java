package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import simscli.jobs.JobFactory;

/**
 * Test: Verifies JobFactory creates correct Jobs.
 */
public class JobFactoryTest {

    /**
     * Tests that Chef job is created correctly.
     */
    @Test
    public void createsKnownJob() {
        assertEquals("Chef", JobFactory.create("Chef").name());
    }

    /**
     * Tests that unknown job names throw exception.
     */
    @Test
    public void unknownJobThrows() {
        assertThrows(IllegalArgumentException.class, () -> JobFactory.create("Wizard"));
    }
}