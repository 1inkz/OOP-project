package simscli.bank;

public class SimpleInterestPolicy implements InterestPolicy {
    private static final double RATE = 0.0005;

    public Money calculateInterest(Money balance) {
        int interest = (int)(balance.getAmount() * RATE);
        return new Money(interest);
    }
}
