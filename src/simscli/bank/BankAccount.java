package simscli.bank;

/**
 * Manages a bank account balance for a Sim.
 * Handles deposits and withdrawals using Money objects.
 */
public class BankAccount {
    private Money balance;

    public BankAccount() {
        this.balance = new Money(0);
    }

    /**
     * Deposits money into the account.
     * @param amount the Money object to deposit
     */
    public void deposit(Money amount) {
        balance = balance.add(amount);
    }

    /**
     * Withdraws money from the account.
     * @param amount the Money object to withdraw
     */
    public void withdraw(Money amount) {
        balance = balance.subtract(amount);
    }

    /**
     * Gets the current account balance.
     * @return current balance as Money object
     */
    public Money getBalance() {
        return balance;
    }
}
