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
        if (sim.getOwnedCar() == null) {
            sim.applyEffect(Effect.none()
                    .plus(NeedType.HUNGER, -10)
                    .plus(NeedType.ENERGY, -10));
        }
    }
}
