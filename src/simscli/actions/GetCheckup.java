package simscli.actions;

import simscli.game.GameContext;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class GetCheckup implements Action {
    private static final int CHECKUP_COST = 10;

    @Override
    public String name() {
        return "Get Checkup ($" + CHECKUP_COST + ")";
    }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (sim.getSimcoin() < CHECKUP_COST) {
            return "Not enough Simcoin! Checkup costs $" + CHECKUP_COST + " but you only have $" + sim.getSimcoin();
        }

        sim.addSimcoin(-CHECKUP_COST);

        sim.applyEffect(Effect.none()
                .plus(NeedType.HUNGER, +20)
                .plus(NeedType.ENERGY, +20)
                .plus(NeedType.HYGIENE, +20)
                .plus(NeedType.SOCIAL, -10)
                .plus(NeedType.FUN, -10));

        return sim.getName() + " got a checkup and is feeling much better! (-$" + CHECKUP_COST + ")";
    }
}
