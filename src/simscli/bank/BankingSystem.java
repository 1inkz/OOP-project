package simscli.bank;

/**
 * BankingSystem manages all banking operations including accounts, loans, and interest.
 * Serves as the primary interface for banking functionality.
 */
public class BankingSystem {
    private static final int LOAN_LIMIT = 5000;
    private final BankService bankService;

    /**
     * Initializes the banking system with default simple interest policy.
     */
    public BankingSystem() {
        this.bankService = new BankService(new SimpleInterestPolicy());
    }

    // Deposit operations
    /**
     * Deposits the specified amount into the account.
     * @param amount the amount to deposit (must be positive)
     * @return true if successful, false otherwise
     */
    public boolean deposit(int amount) {
        try {
            if (amount <= 0) {
                return false;
            }
            bankService.deposit(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the current bank deposit balance.
     * @return the balance in the bank account
     */
    public int getDeposit() {
        return bankService.getBalance();
    }

    // Withdrawal operations
    /**
     * Withdraws the specified amount from the account if sufficient balance exists.
     * @param amount the amount to withdraw
     * @return true if successful, false if insufficient funds
     */
    public boolean withdraw(int amount) {
        try {
            if (amount > bankService.getBalance()) {
                return false;
            }
            bankService.withdraw(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Loan operations
    /**
     * Applies for a loan if within the system's limit.
     * @param amount the amount to borrow (must be positive)
     * @return true if approved, false if exceeds limit
     */
    public boolean applyLoan(int amount) {
        try {
            if (amount <= 0 || bankService.getLoan() + amount > LOAN_LIMIT) {
                return false;
            }
            bankService.applyLoan(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Repays part or all of the outstanding loan.
     * @param amount the amount to repay
     */
    public void repayLoan(int amount) {
        try {
            if (amount > 0 && amount <= bankService.getLoan()) {
                bankService.repayLoan(amount);
            }
        } catch (Exception e) {
            // Silently fail on invalid repayment
        }
    }

    /**
     * Gets the outstanding loan amount.
     * @return the current loan balance
     */
    public int getLoanAmount() {
        return bankService.getLoan();
    }

    // Interest operations
    /**
     * Calculates and applies daily interest to the account balance.
     */
    public void settleInterest() {
    /**
     * Gets the system loan limit cap.
     * @return the maximum allowed loan amount
     */
        bankService.settleInterest();
    }

    // Static utility methods
    public static int getLoanLimit() {
        return LOAN_LIMIT;
    }
}
