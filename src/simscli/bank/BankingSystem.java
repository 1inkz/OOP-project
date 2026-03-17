package simscli.bank;

/**
 * BankingSystem manages all banking operations including accounts, loans, and interest.
 * Serves as the primary interface for banking functionality.
 */
public class BankingSystem {
    private static final int LOAN_LIMIT = 5000;
    private final BankService bankService;

    public BankingSystem() {
        this.bankService = new BankService(new SimpleInterestPolicy());
    }

    // Deposit operations
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

    public int getDeposit() {
        return bankService.getBalance();
    }

    // Withdrawal operations
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

    public void repayLoan(int amount) {
        try {
            if (amount > 0 && amount <= bankService.getLoan()) {
                bankService.repayLoan(amount);
            }
        } catch (Exception e) {
            // Silently fail on invalid repayment
        }
    }

    public int getLoanAmount() {
        return bankService.getLoan();
    }

    // Interest operations
    public void settleInterest() {
        bankService.settleInterest();
    }

    // Static utility methods
    public static int getLoanLimit() {
        return LOAN_LIMIT;
    }
}
