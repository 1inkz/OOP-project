package simscli.sims;

import simscli.game.Game;
import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class AdultSim extends Sim {
    public AdultSim(String name, Game game) {
        super(name, SimType.ADULT, game);
    }

    @Override
	public Effect hourlyDecay() {
        return Effect.none()
                .plus(NeedType.HUNGER,  -5)
                .plus(NeedType.ENERGY,  -5)
                .plus(NeedType.HYGIENE, -3)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -3)
                .plus(NeedType.BLADDER, -3);
    }
}