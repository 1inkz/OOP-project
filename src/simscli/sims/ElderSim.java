package simscli.sims;

import simscli.game.Game;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Elder Sim: faster energy and hygiene drain but less social needs.
 */
public final class ElderSim extends Sim {
    /**
     * Creates an elder Sim.
     * @param name the Sim's name
     * @param game the Game instance
     */
    public ElderSim(String name, Game game) {
        super(name, SimType.ELDER, game);
    }

    @Override
	public Effect hourlyDecay() {
        // Elder loses energy faster, hygiene slightly faster but is experienced and calm
        return Effect.none()
                .plus(NeedType.HUNGER,  -2)
                .plus(NeedType.ENERGY,  -5)
                .plus(NeedType.HYGIENE, -4)
                .plus(NeedType.SOCIAL,  -1)
                .plus(NeedType.FUN,     -1)
                .plus(NeedType.BLADDER, -3);
    }
}