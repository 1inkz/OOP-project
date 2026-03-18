package simscli.bank;

/**
 * Simple interest policy: 0.05% daily interest on deposits.
 */
public class SimpleInterestPolicy implements InterestPolicy {
    private static final double RATE = 0.0005;  // 0.05% per day simple interest

    /**
     * Calculates 0.05% interest on the balance.
     * @param balance the account balance
     * @return calculated interest
     */
    public Money calculateInterest(Money balance) {
        int interest = (int)(balance.getAmount() * RATE);
        return new Money(interest);
    }
}
