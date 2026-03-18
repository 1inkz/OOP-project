package simscli.bank;

/**
 * Immutable Money class representing currency amount.
 * Prevents negative money and provides arithmetic operations.
 */
public class Money {
    private int amount;

    /**
     * Creates a Money object with the specified amount.
     * @param amount the monetary amount
     * @throws IllegalArgumentException if amount is negative
     */
    public Money(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Returns a new Money object with sum of both amounts.
     * @param other the Money to add
     * @return new Money with combined amount
     */
    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    /**
     * Returns a new Money object with difference.
     * @param other the Money to subtract
     * @return new Money with reduced amount
     * @throws IllegalArgumentException if result would be negative
     */
    public Money subtract(Money other) {
        if (other.amount > this.amount) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        return new Money(this.amount - other.amount);
    }
}
