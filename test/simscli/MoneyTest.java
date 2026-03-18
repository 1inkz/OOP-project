package simscli;

import simscli.bank.Money;

/**
 * Test: Verifies Money value object behavior and arithmetic operations.
 */
public class MoneyTest {
    
    public static void testMoneyCreation() {
        Money m = new Money(100);
        assert m.amount() == 100;
        System.out.println("✓ testMoneyCreation");
    }

    public static void testMoneyZero() {
        Money m = new Money(0);
        assert m.amount() == 0;
        System.out.println("✓ testMoneyZero");
    }

    public static void testAddPositiveAmount() {
        Money m1 = new Money(100);
        Money m2 = m1.add(50);
        assert m1.amount() == 100 : "Original should be immutable";
        assert m2.amount() == 150;
        System.out.println("✓ testAddPositiveAmount");
    }

    public static void testSubtractWithSufficientFunds() {
        Money m1 = new Money(100);
        Money m2 = m1.subtract(30);
        assert m1.amount() == 100 : "Original should be immutable";
        assert m2.amount() == 70;
        System.out.println("✓ testSubtractWithSufficientFunds");
    }

    public static void testSubtractAllFunds() {
        Money m1 = new Money(100);
        Money m2 = m1.subtract(100);
        assert m2.amount() == 0;
        System.out.println("✓ testSubtractAllFunds");
    }

    public static void testNegativeMoneyThrows() {
        try {
            new Money(-50);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testNegativeMoneyThrows");
        }
    }

    public static void testSubtractMoreThanFundsThrows() {
        try {
            Money m = new Money(50);
            m.subtract(100);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testSubtractMoreThanFundsThrows");
        }
    }

    public static void testChainedOperations() {
        Money m = new Money(100);
        Money result = m.add(50).subtract(30).add(20);
        assert result.amount() == 140;
        System.out.println("✓ testChainedOperations");
    }

    public static void testLargeAmounts() {
        Money m = new Money(1000000);
        Money m2 = m.add(500000);
        assert m2.amount() == 1500000;
        System.out.println("✓ testLargeAmounts");
    }

    public static void main(String[] args) {
        testMoneyCreation();
        testMoneyZero();
        testAddPositiveAmount();
        testSubtractWithSufficientFunds();
        testSubtractAllFunds();
        testNegativeMoneyThrows();
        testSubtractMoreThanFundsThrows();
        testChainedOperations();
        testLargeAmounts();
    }
}
