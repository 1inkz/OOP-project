package simscli;

import simscli.bank.BankingSystem;

/**
 * Test: Verifies banking operations and loan limit enforcement.
 */
public class BankingSystemTest {
    
    public static void testInitialBalance() {
        BankingSystem banking = new BankingSystem();
        assert banking.getDeposit() == 0;
        System.out.println("✓ testInitialBalance");
    }

    public static void testDepositAmount() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        assert banking.getDeposit() == 100;
        System.out.println("✓ testDepositAmount");
    }

    public static void testMultipleDeposits() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        banking.deposit(50);
        banking.deposit(25);
        assert banking.getDeposit() == 175;
        System.out.println("✓ testMultipleDeposits");
    }

    public static void testWithdrawSufficientFunds() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        banking.withdraw(30);
        assert banking.getDeposit() == 70;
        System.out.println("✓ testWithdrawSufficientFunds");
    }

    public static void testWithdrawAllFunds() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        banking.withdraw(100);
        assert banking.getDeposit() == 0;
        System.out.println("✓ testWithdrawAllFunds");
    }

    public static void testWithdrawMoreThanFundsThrows() {
        try {
            BankingSystem banking = new BankingSystem();
            banking.deposit(50);
            banking.withdraw(100);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testWithdrawMoreThanFundsThrows");
        }
    }

    public static void testApplyLoan() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(1000);
        assert banking.getLoanAmount() == 1000;
        System.out.println("✓ testApplyLoan");
    }

    public static void testLoanAddsToDeposit() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(100);
        banking.applyLoan(500);
        assert banking.getDeposit() == 600;
        assert banking.getLoanAmount() == 500;
        System.out.println("✓ testLoanAddsToDeposit");
    }

    public static void testApplyLoanAtLimit() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(5000);
        assert banking.getLoanAmount() == 5000;
        System.out.println("✓ testApplyLoanAtLimit");
    }

    public static void testApplyLoanExceedsLimit() {
        try {
            BankingSystem banking = new BankingSystem();
            banking.applyLoan(5001);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testApplyLoanExceedsLimit");
        }
    }

    public static void testRepayLoanPartially() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(1000);
        banking.repayLoan(300);
        assert banking.getLoanAmount() == 700;
        System.out.println("✓ testRepayLoanPartially");
    }

    public static void testRepayLoanCompletely() {
        BankingSystem banking = new BankingSystem();
        banking.applyLoan(1000);
        banking.repayLoan(1000);
        assert banking.getLoanAmount() == 0;
        System.out.println("✓ testRepayLoanCompletely");
    }

    public static void testComplexTransactions() {
        BankingSystem banking = new BankingSystem();
        banking.deposit(1000);
        banking.withdraw(200);
        banking.applyLoan(500);
        banking.withdraw(100);
        banking.repayLoan(200);
        
        // 1000 - 200 + 500 - 100 = 1200
        assert banking.getLoanAmount() == 300; // 500 - 200 repaid
        System.out.println("✓ testComplexTransactions");
    }

    public static void main(String[] args) {
        testInitialBalance();
        testDepositAmount();
        testMultipleDeposits();
        testWithdrawSufficientFunds();
        testWithdrawAllFunds();
        testWithdrawMoreThanFundsThrows();
        testApplyLoan();
        testLoanAddsToDeposit();
        testApplyLoanAtLimit();
        testApplyLoanExceedsLimit();
        testRepayLoanPartially();
        testRepayLoanCompletely();
        testComplexTransactions();
    }
}
