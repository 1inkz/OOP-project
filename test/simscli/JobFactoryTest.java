package simscli;

import org.junit.Test;
import simscli.jobs.JobFactory;

import static org.junit.Assert.*;

public class JobFactoryTest {

    @Test
    public void createsKnownJob() {
        assertEquals("Chef", JobFactory.create("Chef").name());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownJobThrows() {
        JobFactory.create("Wizard");
    }
}