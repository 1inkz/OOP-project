package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simscli.bank.LoanAccount;
import simscli.bank.Money;

public class LoanAccountTest {
    private LoanAccount loanAccount;

    @BeforeEach
    public void setUp() {
        loanAccount = new LoanAccount(); 
    }

    @Test
    public void applyLoan_withinLimit_shouldIncreaseBalance() {
        Money loanAmount = new Money(1000);
        
        loanAccount.applyLoan(loanAmount);
        assertEquals(1000, loanAccount.getLoanBalance().getAmount());
    }

    @Test
    public void applyLoan_exceedLimit_shouldThrowException() {
        Money firstLoan = new Money(4000);
        Money secondLoan = new Money(1001);
        loanAccount.applyLoan(firstLoan);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanAccount.applyLoan(secondLoan);
        });
        assertEquals("Loan limit exceeded", exception.getMessage());
        assertEquals(4000, loanAccount.getLoanBalance().getAmount());
    }

    @Test
    public void repayLoan_withinBalance_shouldDecreaseBalance() {
        Money loanAmount = new Money(2000);
        Money repayAmount = new Money(500);
        loanAccount.applyLoan(loanAmount);
        loanAccount.repayLoan(repayAmount);

        assertEquals(1500, loanAccount.getLoanBalance().getAmount());
    }

    @Test
    public void repayLoan_exceedBalance_shouldThrowException() {
        Money loanAmount = new Money(1000);
        Money overRepayAmount = new Money(1500);
        loanAccount.applyLoan(loanAmount);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loanAccount.repayLoan(overRepayAmount);
        });
        assertEquals("Repay exceeds loan", exception.getMessage());
        assertEquals(1000, loanAccount.getLoanBalance().getAmount());
    }

    @Test
    public void repayLoan_fullRepayment_shouldSetBalanceToZero() {
        Money loanAmount = new Money(3000);
        Money fullRepayAmount = new Money(3000);
        loanAccount.applyLoan(loanAmount); 
        loanAccount.repayLoan(fullRepayAmount);
        
        assertEquals(0, loanAccount.getLoanBalance().getAmount());
    }
}