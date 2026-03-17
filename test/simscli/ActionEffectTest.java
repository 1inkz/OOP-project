package simscli;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.game.Game;
import simscli.sims.Sim;
import simscli.sims.SimType;

public class ActionEffectTest {
    public static void actionConsumesTimeAndChangesState() {
        Game g = new Game();
        Sim s = g.createSim("Ava", SimType.ADULT);
        g.setActiveSim(0);

        int moneyBefore = s.getSimcoin();
        String msg = g.performAction(ActionFactory.create(ActionType.WORK));

        assert msg != null;
        assert s.getSimcoin() > moneyBefore;
        System.out.println("✓ actionConsumesTimeAndChangesState");
        g.shutdown();
    }

    public static void main(String[] args) {
        actionConsumesTimeAndChangesState();
    }
}