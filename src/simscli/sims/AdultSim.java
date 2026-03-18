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
                .plus(NeedType.HUNGER,  -3)
                .plus(NeedType.ENERGY,  -3)
                .plus(NeedType.HYGIENE, -2)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -2)
                .plus(NeedType.BLADDER, -3);
    }
}