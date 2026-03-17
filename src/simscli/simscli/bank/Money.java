package simscli.simscli.bank;

public class Money {
    private int amount;

    public Money(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        if (other.amount > this.amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Money(this.amount - other.amount);
    }
}
