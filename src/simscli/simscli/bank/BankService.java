package simscli.simscli.bank;

public class BankService {
    private BankAccount account;
    private LoanAccount loan;
    private InterestPolicy interestPolicy;

    public BankService(InterestPolicy interestPolicy) {
        this.account = new BankAccount();
        this.loan = new LoanAccount();
        this.interestPolicy = interestPolicy;
    }

    public void deposit(int amount) {
        account.deposit(new Money(amount));
    }

    public void withdraw(int amount) {
        account.withdraw(new Money(amount));
    }

    public void applyLoan(int amount) {
        loan.applyLoan(new Money(amount));
    }

    public void repayLoan(int amount) {
        loan.repayLoan(new Money(amount));
    }

    public void settleInterest() {
        Money interest = interestPolicy.calculateInterest(account.getBalance());
        account.deposit(interest);
    }

    public int getBalance() {
        return account.getBalance().getAmount();
    }

    public int getLoan() {
        return loan.getLoanBalance().getAmount();
    }
}