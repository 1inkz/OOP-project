package simscli.sims;

import simscli.stats.Effect;
import simscli.stats.NeedType;

public final class ChildSim extends Sim {
    public ChildSim(String name) {
        super(name, SimType.CHILD);
    }

    @Override
    protected Effect hourlyDecay() {
        // Kids lose energy slower, lose fun slower, but bladder fills faster
        return Effect.none()
                .plus(NeedType.HUNGER,  -4)
                .plus(NeedType.ENERGY,  -4)
                .plus(NeedType.HYGIENE, -2)
                .plus(NeedType.SOCIAL,  -2)
                .plus(NeedType.FUN,     -2)
                .plus(NeedType.BLADDER, -4);
    }
}