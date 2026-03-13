package simscli.bank;

public class BankingSystem {
    private static final double INTEREST_RATE = 0.0005; 
    private static final int LOAN_LIMIT = 5000;

    private int deposit;
    private int loanAmount;

    public BankingSystem() {
        this.deposit = 0;
        this.loanAmount = 0;
    }

    // Deposit simcoin to bank
    public boolean deposit(int amount) {
        if (amount < 0) {
            System.out.println("Deposit amount must be positive.");
            return false;
        }
        this.deposit += amount;
        return true;
    }

    // Withdraw simcoin from bank
    public boolean withdraw(int amount) {
        if (amount < 0) {
            System.out.println("Withdrawal amount must be positive.");
            return false;
        }
        if (amount > this.deposit) {
            System.out.println("Insufficient deposit.");
            return false;
        }
        this.deposit -= amount;
        return true;
    }

    // Apply for loan
    public boolean applyLoan(int amount) {
        if (amount < 0) {
            System.out.println("Loan amount must be positive.");
            return false;
        }
        if (amount > LOAN_LIMIT || (loanAmount+amount) > LOAN_LIMIT) {
            System.out.println("Loan limit exceeded (max $" + LOAN_LIMIT + ").");
            return false;
        }
        this.loanAmount += amount;
        return true;
    }

    // Repay loan
    public boolean repayLoan(int amount) {
        if (amount <= 0) {
            System.out.println("Repay amount must be positive.");
            return false;
        }
        if (amount > this.loanAmount) {
            System.out.println("Repay amount cannot exceed loan balance.");
            return false;
        }
        this.loanAmount -= amount;
        return true;
    }

    // Settle daily interest 
    public void settleInterest() {
        if (this.deposit > 0) {
            int interest = (int) (this.deposit * INTEREST_RATE);
            this.deposit += Math.round(interest);
        }
    }

    public int getDeposit() {
        return deposit;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public static int getLoanLimit() {
        return LOAN_LIMIT;
    }
}