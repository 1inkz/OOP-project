package simscli.bank;

public class SimpleInterestPolicy implements InterestPolicy {
    private static final double RATE = 0.0005;  // 0.05% per day simple interest

    public Money calculateInterest(Money balance) {
        int interest = (int)(balance.getAmount() * RATE);
        return new Money(interest);
    }
}
