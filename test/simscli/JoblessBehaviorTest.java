package simscli;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.SimType;

public class JoblessBehaviorTest {

    public static void newSimStartsJobless() {
        Game g = new Game();
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);
        assert g.activeSim().getJobName().equals("Jobless");
        System.out.println("✓ newSimStartsJobless");
        g.shutdown();
    }

    public static void joblessCannotWork_moneyUnchanged() {
        Game g = new Game();
        g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int before = g.activeSim().getSimcoin();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));
        int after = g.activeSim().getSimcoin();

        assert before == after;
        assert msg.toLowerCase().contains("jobless");
        System.out.println("✓ joblessCannotWork_moneyUnchanged");
        g.shutdown();
    }

    public static void main(String[] args) {
        newSimStartsJobless();
        joblessCannotWork_moneyUnchanged();
    }
}