package simscli.bank;

/**
 * Core banking service managing accounts, loans, and interest.
 * Delegates to BankAccount and LoanAccount for specific operations.
 */
public class BankService {
    private BankAccount account;
    private LoanAccount loan;
    private InterestPolicy interestPolicy;

    public BankService(InterestPolicy interestPolicy) {
        this.account = new BankAccount();
        this.loan = new LoanAccount();
        this.interestPolicy = interestPolicy;
    }

    /**
     * Deposits the specified amount into the account.
     * @param amount the amount to deposit
     */
    public void deposit(int amount) {
        account.deposit(new Money(amount));
    }

    /**
     * Withdraws the specified amount from the account.
     * @param amount the amount to withdraw
     */
    public void withdraw(int amount) {
        account.withdraw(new Money(amount));
    }

    /**
     * Applies for a new loan.
     * @param amount the loan amount
     */
    public void applyLoan(int amount) {
        loan.applyLoan(new Money(amount));
    }

    /**
     * Repays part of the loan.
     * @param amount the repayment amount
     */
    public void repayLoan(int amount) {
        loan.repayLoan(new Money(amount));
    }

    /**
     * Calculates and applies daily interest to the account.
     */
    public void settleInterest() {
        Money interest = interestPolicy.calculateInterest(account.getBalance());
        account.deposit(interest);
    }

    /**
     * Gets the current account balance.
     * @return balance amount
     */
    public int getBalance() {
        return account.getBalance().getAmount();
    }

    /**
     * Gets the outstanding loan amount.
     * @return loan balance
     */
    public int getLoan() {
        return loan.getLoanBalance().getAmount();
    }
}