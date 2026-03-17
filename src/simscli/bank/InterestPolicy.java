package simscli.bank;

public interface InterestPolicy {
    Money calculateInterest(Money balance);
}
