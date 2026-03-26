package simscli.actions.simple;

import simscli.actions.Action;
import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.NeedType;

/**
 * Health action: Emergency care fully restores all needs for a premium fee.
 */
public final class GetCheckup implements Action {
    private static final int EMERGENCY_CARE_COST = 400;

    @Override
    public String name() {
        return "Emergency Care ($" + EMERGENCY_CARE_COST + ")";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getSimcoin() < EMERGENCY_CARE_COST) {
            return "Not enough Simcoin! Emergency Care costs $" + EMERGENCY_CARE_COST + " but you only have $" + sim.getSimcoin();
        }

        sim.addSimcoin(-EMERGENCY_CARE_COST);

        sim.getNeeds().set(NeedType.HUNGER, 90);
        sim.getNeeds().set(NeedType.ENERGY, 90);
        sim.getNeeds().set(NeedType.HYGIENE, 90);
        sim.getNeeds().set(NeedType.SOCIAL, 90);
        sim.getNeeds().set(NeedType.FUN, 90);
        sim.getNeeds().set(NeedType.BLADDER, 90);

        return sim.getName() + " received Emergency Care and all needs are fully restored! (-$" + EMERGENCY_CARE_COST + ")";
    }
}
