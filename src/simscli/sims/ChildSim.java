package simscli.sims;

import simscli.game.Game;
import simscli.stats.Effect;
import simscli.stats.NeedType;

/**
 * Child Sim: slower energy drain but faster hunger and bladder.
 */
public final class ChildSim extends Sim {
    /**
     * Creates a child Sim.
     * @param name the Sim's name
     * @param game the Game instance
     */
    public ChildSim(String name, Game game) {
        super(name, SimType.CHILD, game);
    }

    @Override
	public Effect hourlyDecay() {
        // Kids lose energy slower, lose fun slower, but bladder fills faster and more hungry
        return Effect.none()
                .plus(NeedType.HUNGER,  -4)
                .plus(NeedType.ENERGY,  -2)
                .plus(NeedType.HYGIENE, -3)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -2)
                .plus(NeedType.BLADDER, -4);
    }
}