package simscli.simscli.bank;

public class BankAccount {
    private Money balance;

    public BankAccount() {
        this.balance = new Money(0);
    }

    public void deposit(Money amount) {
        balance = balance.add(amount);
    }

    public void withdraw(Money amount) {
        balance = balance.subtract(amount);
    }

    public Money getBalance() {
        return balance;
    }
}
