package simscli;

import simscli.bank.LoanAccount;

/**
 * Test: Verifies LoanAccount loan limit enforcement (5000 max).
 */
public class LoanAccountTest {
    
    public static void testInitialLoanAmount() {
        LoanAccount loan = new LoanAccount();
        assert loan.getLoanBalance() == 0;
        System.out.println("✓ testInitialLoanAmount");
    }

    public static void testApplySmallLoan() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(500);
        assert loan.getLoanBalance() == 500;
        System.out.println("✓ testApplySmallLoan");
    }

    public static void testApplyLoanAtLimit() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(5000);
        assert loan.getLoanBalance() == 5000;
        System.out.println("✓ testApplyLoanAtLimit");
    }

    public static void testApplyLoanExceedsMaximum() {
        try {
            LoanAccount loan = new LoanAccount();
            loan.applyLoan(5001);
            assert false : "Should throw IllegalArgumentException";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testApplyLoanExceedsMaximum");
        }
    }

    public static void testApplyLoanExceedsFromExisting() {
        try {
            LoanAccount loan = new LoanAccount();
            loan.applyLoan(3000);
            loan.applyLoan(2500); // Would total 5500
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testApplyLoanExceedsFromExisting");
        }
    }

    public static void testApplyMultipleLoansWithinLimit() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(1000);
        assert loan.getLoanBalance() == 1000;
        
        loan.applyLoan(2000);
        assert loan.getLoanBalance() == 3000;
        
        loan.applyLoan(2000);
        assert loan.getLoanBalance() == 5000;
        System.out.println("✓ testApplyMultipleLoansWithinLimit");
    }

    public static void testRepayPartialLoan() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(1000);
        loan.repayLoan(300);
        assert loan.getLoanBalance() == 700;
        System.out.println("✓ testRepayPartialLoan");
    }

    public static void testRepayFullLoan() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(1000);
        loan.repayLoan(1000);
        assert loan.getLoanBalance() == 0;
        System.out.println("✓ testRepayFullLoan");
    }

    public static void testRepayMoreThanOwed() {
        try {
            LoanAccount loan = new LoanAccount();
            loan.applyLoan(500);
            loan.repayLoan(600);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testRepayMoreThanOwed");
        }
    }

    public static void testRepayWhenNoLoan() {
        try {
            LoanAccount loan = new LoanAccount();
            loan.repayLoan(100);
            assert false : "Should throw";
        } catch (IllegalArgumentException e) {
            System.out.println("✓ testRepayWhenNoLoan");
        }
    }

    public static void testLoanCycleMultiple() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(2000);
        loan.repayLoan(500);
        
        assert loan.getLoanBalance() == 1500;
        
        loan.applyLoan(2000);
        assert loan.getLoanBalance() == 3500;
        
        loan.repayLoan(1500);
        assert loan.getLoanBalance() == 2000;
        System.out.println("✓ testLoanCycleMultiple");
    }

    public static void testApplyAtMaxThenRepayPartial() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(5000);
        loan.repayLoan(1000);
        assert loan.getLoanBalance() == 4000;
        System.out.println("✓ testApplyAtMaxThenRepayPartial");
    }

    public static void testCanApplyMoreAfterPartialRepay() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(3000);
        loan.repayLoan(1000);
        loan.applyLoan(3000); // Now can apply more to reach 5000
        assert loan.getLoanBalance() == 5000;
        System.out.println("✓ testCanApplyMoreAfterPartialRepay");
    }

    public static void testMaxBoundaryConditions() {
        LoanAccount loan = new LoanAccount();
        loan.applyLoan(1000);
        loan.repayLoan(1000);
        
        // Should be able to apply fresh max loan
        loan.applyLoan(5000);
        assert loan.getLoanBalance() == 5000;
        System.out.println("✓ testMaxBoundaryConditions");
    }

    public static void main(String[] args) {
        testInitialLoanAmount();
        testApplySmallLoan();
        testApplyLoanAtLimit();
        testApplyLoanExceedsMaximum();
        testApplyLoanExceedsFromExisting();
        testApplyMultipleLoansWithinLimit();
        testRepayPartialLoan();
        testRepayFullLoan();
        testRepayMoreThanOwed();
        testRepayWhenNoLoan();
        testLoanCycleMultiple();
        testApplyAtMaxThenRepayPartial();
        testCanApplyMoreAfterPartialRepay();
        testMaxBoundaryConditions();
    }
}
