package simscli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import simscli.bank.Money;

public class MoneyTest {

    @Test
    public void createMoney_negativeAmount_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Money(-100);
        });
        assertEquals("Money cannot be negative", exception.getMessage());
    }

    @Test
    public void addMoney_shouldReturnNewInstanceWithSum() {
        Money money1 = new Money(100);
        Money money2 = new Money(200);
        Money sum = money1.add(money2);
        
        assertEquals(300, sum.getAmount());
        assertEquals(100, money1.getAmount()); 
        assertEquals(200, money2.getAmount()); 
    }

    @Test
    public void subtractMoney_shouldReturnNewInstanceWithDifference() {
        Money money1 = new Money(500);
        Money money2 = new Money(300);
        Money difference = money1.subtract(money2);
        
        assertEquals(200, difference.getAmount());
        assertEquals(500, money1.getAmount()); 
        assertEquals(300, money2.getAmount()); 
    }

    @Test
    public void subtractMoney_insufficientFunds_shouldThrowException() {
        Money smallMoney = new Money(200);
        Money largeMoney = new Money(300);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            smallMoney.subtract(largeMoney);
        });
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    public void getAmount_shouldReturnCorrectValue() {
        Money money = new Money(1500);
        assertEquals(1500, money.getAmount());
    }
}