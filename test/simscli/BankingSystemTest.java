package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.bank.BankingSystem;

/**
 * Test: Verifies banking operations and loan limit enforcement.
 */
public class BankingSystemTest {

    @Test
    public void testInitialBalance() {
        BankingSystem banking = new BankingSystem();
        assertEquals(0, banking.getDeposit());
    }

    @Test
    public void testDepositAmount() {
        BankingSystem banking = new BankingSystem();
        assertTrue(banking.deposit(100));
        assertEquals(100, banking.getDeposit());
    }

    @Test
    public void testMultipleDeposits() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        banking.deposit(50);
        banking.deposit(25);
        assertEquals(175, banking.getDeposit());
    }

    @Test
    public void testWithdrawSufficientFunds() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        assertTrue(banking.withdraw(30));
        assertEquals(70, banking.getDeposit());
    }

    @Test
    public void testWithdrawAllFunds() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        assertTrue(banking.withdraw(100));
        assertEquals(0, banking.getDeposit());
    }

    @Test
    public void testWithdrawMoreThanFundsReturnsFalse() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(50);
        assertFalse(banking.withdraw(100));
        assertEquals(50, banking.getDeposit());
    }

    @Test
    public void testApplyLoan() {
        BankingSystem banking = new BankingSystem();
        assertTrue(banking.applyLoan(1000));
        assertEquals(1000, banking.getLoanAmount());
    }

    @Test
    public void testApplyLoanAtLimit() {
        BankingSystem banking = new BankingSystem();
        assertTrue(banking.applyLoan(5000));
        assertEquals(5000, banking.getLoanAmount());
    }

    @Test
    public void testApplyLoanExceedsLimitReturnsFalse() {
        BankingSystem banking = new BankingSystem();
        assertFalse(banking.applyLoan(5001));
        assertEquals(0, banking.getLoanAmount());
    }

    @Test
    public void testRepayLoanPartially() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(1000);
        banking.repayLoan(300);
        assertEquals(700, banking.getLoanAmount());
    }

    @Test
    public void testRepayLoanCompletely() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(1000);
        banking.repayLoan(1000);
        assertEquals(0, banking.getLoanAmount());
    }

    @Test
    public void testComplexTransactions() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(1000);
        banking.withdraw(200);
        banking.applyLoan(500);
        banking.withdraw(100);
        banking.repayLoan(200);

        assertEquals(300, banking.getLoanAmount());
        assertEquals(700, banking.getDeposit());
    }
}
