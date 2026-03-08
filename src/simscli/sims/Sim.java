package simscli.sims;

import simscli.jobs.*;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.location.Location;
import java.util.Map;
import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.House;
import simscli.bank.BankingSystem;
import simscli.game.Game;

public abstract class Sim {
    private final String name;
    private final SimType type;
    private final Needs needs = new Needs();
    private boolean alive = true;
    
    private int simcoin = 50;
    private int jobLevel = 1;
    private Job job;
    private Location location;
    
    private final BankingSystem bankingSystem = new BankingSystem();
    private Asset ownedCar;
    private Asset ownedHouse;
    
    private int startDay; 

    protected Sim(String name, SimType type, Game game) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name required");
        this.name = name.trim();
        this.type = type;
        this.job = new JoblessJob();
        this.startDay = game.getClock().getDayNumber(); 
    }

    public final String getName() { return name; }
    public final SimType getType() { return type; }
    public final boolean isAlive() { return alive; }
    public Needs getNeeds() { return needs; }
    
    
    // Start: Time
    public int getPersonalDay(Game game) {
        if (game == null || game.getClock() == null) {
            return 1;
        }
        int currentGameDay = game.getClock().getDayNumber();
        int personalDay = currentGameDay - this.startDay + 1;
        return Math.max(1, personalDay);
    }
    
    public int getStartDay() {
        return this.startDay;
    }
    
    public void setStartDay(int startDay) {
        this.startDay = Math.max(1, startDay);
    }
    // End: Time
    
    
    // Start: Job
    public final int getJobLevel() { return jobLevel; }
    public final String getJobName() { return job.name(); }

    public Job getJob() {
        return job != null ? job : new JoblessJob();
    }
    
    public final void setJob(Job newJob) {
        if (newJob == null) {
            this.job = new JoblessJob(); 
        } else {
            this.job = newJob;
        }
    }
    
    public void setJobLevel(int jobLevel) {
        if (jobLevel >= 1) {
            this.jobLevel = jobLevel;
        }
    }
    
    public final String work() {
        if (!alive) {
        	return name + " is not available.";
        }

        if (!job.canWork()) {
            return name + " is jobless. Get a job first!";
        }

        // working drains some needs
	    Effect cost = Effect.none()
	            .plus(NeedType.ENERGY, -20)
	            .plus(NeedType.HUNGER, -15);
	    applyEffect(cost);
	
	    int earned = (int) Math.round(job.salary(jobLevel));
	    simcoin += earned;
	    jobLevel++;

        return name + " worked as " + job.name() + " and earned $" + earned + ".";
    }
    
    // End: Job

    
    // Start: Stats
    public final int moodScore() {
        // Simple average to show “mood”
        int sum = 0;
        for (NeedType t : NeedType.values()) sum += needs.get(t);
        return sum / NeedType.values().length;
    }

    public final boolean isCritical(NeedType t) {
        return needs.isCritical(t);
    }

    private void checkDeathConditions() {
        // If any need hits 0, sim “dies/leaves” (simple rule)
        for (NeedType t : NeedType.values()) {
            if (needs.isZero(t)) {
                alive = false;
                return;
            }
        }
    }
    
    public void applyEffect(Effect effect) {
        for (Map.Entry<NeedType, Integer> entry : effect.deltas().entrySet()) {
            needs.add(entry.getKey(), entry.getValue());
        }
        checkDeathConditions(); // Immediate elimination (no delay)
    }
    // End: Stats
    
    
    // Start: Location
    public final Location getLocation() {
    	return location;
    }

    public final void setLocation(Location location) {
    	if (location == null) {
    		throw new IllegalArgumentException("location required");
    	}
    	this.location = location;
    }
    // End: Location
    
    
    // Start: Asset
    public boolean buyAsset(Asset asset) {
        int totalCost = asset.getValue();
        int downPayment = asset.isCar() ? 400 : 1500; 
        int loanAmount = totalCost - downPayment;
        if (loanAmount < 0) loanAmount = 0;

        if (simcoin < downPayment) {
            System.out.println("Insufficient Simcoin for down payment!");
            return false;
        }
        if (loanAmount > (BankingSystem.getLoanLimit()-bankingSystem.getLoanAmount())) {
            System.out.println("Loan limit exceeded");
            return false;
        }
        
        simcoin -= downPayment;
        bankingSystem.applyLoan(loanAmount);

        if (asset.isCar()) {
        	ownedCar = asset;
        }
        else if (asset.isHouse()) {
        	ownedHouse = asset;
        }
        return true;
    }

    public String sellAsset(Asset asset) {
        if (asset == null) {
        	return "No asset to sell!";
        }

        int sellValue = asset.sellValue();
        int loanToRepay = Math.min(bankingSystem.getLoanAmount(), sellValue);

        bankingSystem.repayLoan(loanToRepay);
        simcoin += (sellValue - loanToRepay);

        if (asset.isCar()) {
        	ownedCar = null;
        }
        else if (asset.isHouse()) {
        	ownedHouse = null;
        }

        return name + " sold " + asset.getName() + " for $" + sellValue + " (Profit after loan: $" + (sellValue - loanToRepay) + ")";
    }
    
    public Asset getOwnedCar() {
        return ownedCar;
    }
    
    public void setOwnedCar(Car car) {
        this.ownedCar = car;
    }

    public Asset getOwnedHouse() {
        return ownedHouse;
    }
    
    public void setOwnedHouse(House house) {
        this.ownedHouse = house;
    }
    // End: Asset
    
    
    // Start: Bank
    public BankingSystem getBankingSystem() { return bankingSystem; }
    public void settleBankInterest() { bankingSystem.settleInterest(); }
    public int getBankDeposit() { return bankingSystem.getDeposit(); }
    public void setBankDeposit(int v) { bankingSystem.deposit(v); }
    public final int getSimcoin() { return simcoin; }
    public void setSimcoin(int v) { this.simcoin = v; }
    public int getLoanAmount() { return bankingSystem.getLoanAmount(); }
    public void setLoanAmount(int v) { bankingSystem.applyLoan(v); }
    
    
    public final void earnSimcoin(int amount) {
    	simcoin = Math.max(0, simcoin + amount);
    }

    public boolean spendSimcoin(int amount) {
        if (amount <= 0 || simcoin < amount) {
        	return false;
        }
        simcoin -= amount;
        return true;
    }
    
    public boolean depositToBank(int amount) {
        if (amount < 0) {
            System.out.println("Deposit amount must be positive!");
            return false;
        }
        if (this.simcoin < amount) {
            System.out.println("Insufficient Simcoin to deposit!");
            return false;
        }
        spendSimcoin(amount);
        bankingSystem.deposit(amount);
        return true;
    }

    public boolean withdrawFromBank(int amount) {
        if (amount < 0) {
            System.out.println("Withdrawal amount must be positive!");
            return false;
        }
        if (bankingSystem.withdraw(amount)) {
        	earnSimcoin(amount);
            return true;
        }
        return false;
    }
    // End: Bank
    
   
    public final String summaryLine() {
        return String.format(
                "%s (%s) | $%d | Job=%s L%d | H=%d E=%d Hy=%d S=%d F=%d B=%d",
                name, type, simcoin, getJobName(), jobLevel,
                needs.get(NeedType.HUNGER),
                needs.get(NeedType.ENERGY),
                needs.get(NeedType.HYGIENE),
                needs.get(NeedType.SOCIAL),
                needs.get(NeedType.FUN),
                needs.get(NeedType.BLADDER)
        );
    }

    /** Each subtype defines its decay pace. */
    public abstract Effect hourlyDecay();
}