public class Bank {
    private float simcoin, interestRate, loanAmount;
    private String bankName;

    public Bank(){
        simcoin = 0;
        interestRate = 0;
    }

    public void setBankName(String newBankName){
        this.bankName = newBankName;
    }

    public String getBankName(){
        return this.bankName;
    }


    public boolean deposit(double amt){
        if(amt <= 0){
            System.out.println("Please enter a positive amount");
            return false;
        }else{
            simcoin += amt;
            return true;
        }
        

    }

    public boolean withdraw(double amt){
        if(simcoin > 0 && amt < simcoin){
            simcoin -= amt;
            System.out.println("Withdrawal Successful!");
            return true;
        }else{
            System.out.println("Insufficient balance!");
            return false;
        }
    }

    public double getBalance(){
        return simcoin;
    }

    public boolean takeLoan(double amt){
        if(amt <= 0){
            System.out.println("Please enter a positive loan value!");
            return false;
        }else{
            loanAmount += amt;
            simcoin += amt;
            return true;
        }
    }

    public boolean repayLoan(double amt){
        if(amt < simcoin){
            System.out.println("You do not have sufficient simcoins!");
            return false;
        }else if(amt > loanAmount){
            System.out.println("You are paying more than required.");
            return false;
        }else{
            System.out.println("Payment successful");
            loanAmount -= amt;
            simcoin -= amt;
            System.out.println("Your loan amount is left: " + loanAmount);
            return true;
        }
    }

}
