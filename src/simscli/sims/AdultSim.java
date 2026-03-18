package simscli.sims;

import simscli.game.Game;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Adult Sim: balanced stat decay and earnings.
 */
public final class AdultSim extends Sim {
    /**
     * Creates an adult Sim.
     * @param name the Sim's name
     * @param game the Game instance
     */
    public AdultSim(String name, Game game) {
        super(name, SimType.ADULT, game);
    }

    @Override
	public Effect hourlyDecay() {
        return Effect.none()
                .plus(NeedType.HUNGER,  -3)
                .plus(NeedType.ENERGY,  -3)
                .plus(NeedType.HYGIENE, -2)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -2)
                .plus(NeedType.BLADDER, -3);
    }
}