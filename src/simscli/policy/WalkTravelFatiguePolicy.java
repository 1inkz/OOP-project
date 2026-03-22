package simscli.policy;

import simscli.location.Location;
import simscli.sims.Sim;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Applies travel fatigue when Sim travels without owning a car.
 */
public final class WalkTravelFatiguePolicy implements TravelEffectPolicy {
    @Override
    public void onTravel(Sim sim, Location from, Location to) {
        double multiplier = sim.getTravelFatigueMultiplier();
        int hungerPenalty = (int) Math.round(-10 * multiplier);
        int energyPenalty = (int) Math.round(-10 * multiplier);

        sim.applyEffect(Effect.none()
                .plus(NeedType.HUNGER, hungerPenalty)
                .plus(NeedType.ENERGY, energyPenalty));
    }
}
