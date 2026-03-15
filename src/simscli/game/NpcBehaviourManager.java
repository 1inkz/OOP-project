package simscli.game;

import simscli.actions.ActionFactory;
import simscli.actions.ActionType;
import simscli.sims.Sim;
import simscli.stats.NeedType;

public final class NpcBehaviourManager {

    public void autoHelpIfCritical(Sim sim, Game game) {
        if (!sim.isAlive()) return;

        if (sim.isCritical(NeedType.HUNGER)) {
            ActionFactory.create(ActionType.EAT_SNACK).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.ENERGY)) {
            ActionFactory.create(ActionType.NAP).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.BLADDER)) {
            ActionFactory.create(ActionType.USE_TOILET).perform(sim, new GameContext(game));
        } else if (sim.isCritical(NeedType.HYGIENE)) {
            if (sim.getOwnedHouse() != null) {
                ActionFactory.create(ActionType.BRUSH_TEETH).perform(sim, new GameContext(game));
            } else {
                ActionFactory.create(ActionType.CLEAN_PUBLIC).perform(sim, new GameContext(game));
            }
        }
    }
}