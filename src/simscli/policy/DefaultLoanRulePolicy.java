package simscli.policy;

import java.util.List;
import simscli.game.Game;
import simscli.game.GameLogger;
import simscli.sims.Sim;

/**
 * Default loan overdue policy: repossession after 60 days and bankruptcy death after 80.
 */
public final class DefaultLoanRulePolicy implements LoanRulePolicy {
    @Override
    public void apply(List<Sim> sims, Sim activeSim, Game game, GameLogger logger) {
        for (Sim sim : sims) {
            if (!sim.isAlive() || !sim.hasAssetLoan()) {
                continue;
            }

            int overdueDays = sim.getLoanOverdueDays(game);

            if (overdueDays >= 60 && overdueDays < 80) {
                String repossessionMsg = sim.repossessAsset();

                if (sim == activeSim) {
                    logger.error(repossessionMsg);
                } else {
                    sim.addPendingLoanMessage(repossessionMsg);
                }
            }
            
            if (overdueDays >= 80 && (sim.getBankingComponent().isInsolvent(sim.getLoanAmount()))) {
            	if (sim.haveAsset()) {
                    String repossessionMsg = sim.repossessAsset();

                    if (sim == activeSim) {
                        logger.error(repossessionMsg);
                    } else {
                        sim.addPendingLoanMessage(repossessionMsg);
                    }
            	} else {
                    sim.setAlive(false);
                    sim.clearPendingLoanMessages();
            	}
            }
        }
    }
}
