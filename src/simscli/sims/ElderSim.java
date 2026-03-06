package simscli.sims;

import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class ElderSim extends Sim {
    public ElderSim(String name) {
        super(name, SimType.ELDER);
    }

    @Override
    protected Effect hourlyDecay() {
        // Elder loses energy faster, hygiene slightly faster
        return Effect.none()
                .plus(NeedType.HUNGER,  -5)
                .plus(NeedType.ENERGY,  -7)
                .plus(NeedType.HYGIENE, -4)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -3)
                .plus(NeedType.BLADDER, -3);
    }
}