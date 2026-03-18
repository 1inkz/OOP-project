package simscli.bank;

/**
 * Manages Sim loans with a 5000 maximum limit.
 */
public class LoanAccount {
    private Money loanBalance;
    private final int LOAN_LIMIT = 5000;

    public LoanAccount() {
        this.loanBalance = new Money(0);
    }

    /**
     * Adds a new loan amount.
     * @param amount the loan amount
     * @throws IllegalArgumentException if total would exceed limit
     */
    public void applyLoan(Money amount) {
        int newTotal = loanBalance.getAmount() + amount.getAmount();
        if (newTotal > LOAN_LIMIT) {
            throw new IllegalArgumentException("Loan limit exceeded");
        }
        loanBalance = new Money(newTotal);
    }

    /**
     * Repays part of the loan.
     * @param amount the repayment amount
     * @throws IllegalArgumentException if exceeds loan balance
     */
    public void repayLoan(Money amount) {
        if (amount.getAmount() > loanBalance.getAmount()) {
            throw new IllegalArgumentException("Repay exceeds loan");
        }
        loanBalance = new Money(loanBalance.getAmount() - amount.getAmount());
    }

    /**
     * Gets the current loan balance.
     * @return outstanding loan
     */
    public Money getLoanBalance() {
        return loanBalance;
    }
}