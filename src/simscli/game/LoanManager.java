package simscli.game;

import java.util.List;
import simscli.sims.Sim;

/**
 * Manages loan overdues, repossessions, and insolvency checks.
 * Encapsulates all loan-related game rules.
 */
public class LoanManager {
    private final GameLogger logger;

    public LoanManager(GameLogger logger) {
        this.logger = logger;
    }

    /**
     * Checks for loan overdue situations and applies consequences.
     * Handles repossession and insolvency elimination.
     */
    public void checkLoanOverdueRules(List<Sim> sims, Sim activeSim, Game game) {
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

            if (overdueDays >= 80 && sim.isInsolvent()) {
                sim.setAlive(false);
                logger.error("\u001B[31m[Insolvent]\u001B[0m " + sim.getName() + " died from bankruptcy");
                sim.clearPendingLoanMessages();
            }
        }
    }
}
