package simscli.sims;

import simscli.bank.BankingSystem;

/**
 * Manages Sim banking operations: deposits, loans, simcoin, and wealth.
 * Separates financial logic from core Sim state.
 */
public class SimBankingComponent {
    private int simcoin;
    private final BankingSystem bankingSystem;

    public SimBankingComponent(int initialSimcoin) {
        this.simcoin = initialSimcoin;
        this.bankingSystem = new BankingSystem();
    }

    // Simcoin
    /**
     * Gets on-hand Simcoin amount.
     * @return current Simcoin
     */
    public int getSimcoin() {
        return simcoin;
    }

    public void setSimcoin(int amount) {
        this.simcoin = amount;
    }

    /**
     * Earns Simcoin, ensuring non-negative result.
     * @param amount amount to gain
     */
    public void earnSimcoin(int amount) {
        simcoin = Math.max(0, simcoin + amount);
    }

    /**
     * Spends Simcoin if sufficient balance exists.
     * @param amount amount to spend
     * @return true if successful
     */
    public boolean spendSimcoin(int amount) {
        if (amount <= 0 || simcoin < amount) {
            return false;
        }
        simcoin -= amount;
        return true;
    }

    // Banking
    public BankingSystem getBankingSystem() {
        return bankingSystem;
    }

    public void settleBankInterest() {
        bankingSystem.settleInterest();
    }

    public int getBankDeposit() {
        return bankingSystem.getDeposit();
    }

    public void setBankDeposit(int amount) {
        bankingSystem.deposit(amount);
    }

    public int getLoanAmount() {
        return bankingSystem.getLoanAmount();
    }

    public void applyLoan(int amount) {
        bankingSystem.applyLoan(amount);
    }

    public void repayLoan(int amount) {
        bankingSystem.repayLoan(amount);
    }

    /**
     * Calculates total wealth (Simcoin + deposits).
     * @return total wealth amount
     */
    public int getTotalWealth() {
        return simcoin + getBankDeposit();
    }

    public boolean isInsolvent(int loanAmount) {
        return loanAmount > getTotalWealth();
    }
}
