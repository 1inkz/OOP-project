package simscli.bank;

/**
 * Interface for interest calculation policy.
 */
public interface InterestPolicy {
    /**
     * Calculates interest on the given balance.
     * @param balance the account balance
     * @return calculated interest amount
     */
    Money calculateInterest(Money balance);
}
