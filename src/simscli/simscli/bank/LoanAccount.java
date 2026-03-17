package simscli.simscli.bank;

public class LoanAccount {
    private Money loanBalance;
    private final int LOAN_LIMIT = 5000;

    public LoanAccount() {
        this.loanBalance = new Money(0);
    }

    public void applyLoan(Money amount) {
        int newTotal = loanBalance.getAmount() + amount.getAmount();
        if (newTotal > LOAN_LIMIT) {
            throw new IllegalArgumentException("Loan limit exceeded");
        }
        loanBalance = new Money(newTotal);
    }

    public void repayLoan(Money amount) {
        if (amount.getAmount() > loanBalance.getAmount()) {
            throw new IllegalArgumentException("Repay exceeds loan");
        }
        loanBalance = new Money(loanBalance.getAmount() - amount.getAmount());
    }

    public Money getLoanBalance() {
        return loanBalance;
    }
}