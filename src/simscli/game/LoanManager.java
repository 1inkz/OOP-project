package simscli.game;

import java.util.List;
import simscli.policy.DefaultLoanRulePolicy;
import simscli.policy.LoanRulePolicy;
import simscli.sims.Sim;

/**
 * Manages loan overdues, repossessions, and insolvency checks.
 * Encapsulates all loan-related game rules.
 */
public class LoanManager {
    private final GameLogger logger;
    private final LoanRulePolicy loanRulePolicy;

    public LoanManager(GameLogger logger) {
        this(logger, new DefaultLoanRulePolicy());
    }

    public LoanManager(GameLogger logger, LoanRulePolicy loanRulePolicy) {
        this.logger = logger;
        this.loanRulePolicy = loanRulePolicy;
    }

    /**
     * Checks for loan overdue situations and applies consequences.
     * Handles repossession and insolvency elimination.
     */
    public void checkLoanOverdueRules(List<Sim> sims, Sim activeSim, Game game) {
        loanRulePolicy.apply(sims, activeSim, game, logger);
    }
}
