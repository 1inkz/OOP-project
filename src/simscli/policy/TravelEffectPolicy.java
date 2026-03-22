package simscli.policy;

import simscli.location.Location;
import simscli.sims.Sim;

/**
 * Policy contract for side effects when a Sim travels between locations.
 */
public interface TravelEffectPolicy {
    void onTravel(Sim sim, Location from, Location to);
}
