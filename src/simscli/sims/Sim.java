package simscli.sims;

import simscli.jobs.*;
import simscli.stats.Effect;
import simscli.stats.NeedType;
import simscli.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private Job job;
    private final Map<String, Integer> jobLevels = new HashMap<>();
    private Location location;
    
    private final BankingSystem bankingSystem = new BankingSystem();
    private Asset ownedCar;
    private Asset ownedHouse;
    
    private int startDay; 
    private int loanStartDay = 0;
    private Game game;
    public void setGame(Game game) {this.game = game; }
    public Game getGame() {return game;}

    protected Sim(String name, SimType type, Game game) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name required");
        this.name = name.trim();
        this.type = type;
        this.job = new JoblessJob();
        this.startDay = game.getClock().getDayNumber(); 
        this.game = game;
        jobLevels.put("Jobless", 1);
    }

    public final String getName() { return name; }
    public final SimType getType() { return type; }
    public final boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public Needs getNeeds() { return needs; }
   
    /** Each subtype defines its decay pace. */
    public abstract Effect hourlyDecay();
    
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
    
    public int getLoanStartDay() {
        return this.loanStartDay;
    }
    
    public void setLoanStartDay(int loanStartDay) {
        this.loanStartDay = loanStartDay;
    }
    // End: Time
    
    
    // Start: Job
    public Map<String, Integer> getAllJobLevels() { return new HashMap<>(jobLevels); }
    public final String getJobName() { return job.name(); }

    public Job getJob() {
        return job != null ? job : new JoblessJob();
    }
    
    public final void setJob(Job newJob) {
        if (newJob == null) {
            this.job = new JoblessJob(); 
        } else {
            this.job = newJob;
            String jobName = newJob.name();
            if (!jobLevels.containsKey(jobName)) {
                jobLevels.put(jobName, 1);
            }
        }
    }
    
    public final int getJobLevel() {
        String currentJobName = job.name();
        return jobLevels.getOrDefault(currentJobName, 1);
    }
    
    public void setJobLevel(int jobLevel) {
        if (jobLevel >= 1) {
            String currentJobName = job.name();
            jobLevels.put(currentJobName, jobLevel);
        }
    }
    
    // Load game use
    public void setAllJobLevels(Map<String, Integer> jobLevels) {
        if (jobLevels != null) {
            this.jobLevels.clear();
            this.jobLevels.putAll(jobLevels);
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
	
	    int earned = (int) Math.round(job.salary(getJobLevel()));
	    simcoin += earned;
	    setJobLevel(getJobLevel() + 1);

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
        boolean buySuccess = false;

        if (simcoin >= asset.getValue()) {
        	simcoin -= asset.getValue();
        	buySuccess = true;
        }
        else {
            if (simcoin < downPayment) {
                System.out.println("Insufficient Simcoin for down payment!");
                return false;
            }
            
            if (loanAmount > (BankingSystem.getLoanLimit()- getLoanAmount())) {
                System.out.println("Loan limit exceeded");
                return false;
            }
            
            simcoin -= downPayment;
            bankingSystem.applyLoan(loanAmount);
            buySuccess = true;
        }
        
        if (buySuccess && loanAmount > 0 && this.loanStartDay == 0) {
            this.loanStartDay = game.getClock().getDayNumber();
        }

        if (asset.isCar()) { ownedCar = asset; }
        else if (asset.isHouse()) { ownedHouse = asset; }
        
        return true;
    }

    public String sellAsset(Asset asset) {
        if (asset == null) {
        	return "No asset to sell!";
        }

        int sellValue = asset.sellValue();
        int loanToRepay = Math.min(getLoanAmount(), sellValue);

        bankingSystem.repayLoan(loanToRepay);
        if (getLoanAmount() == 0) {
            this.loanStartDay = 0;
        }
        
        simcoin += (sellValue - loanToRepay);

        if (asset.isCar()) {
        	ownedCar = null;
        }
        else if (asset.isHouse()) {
        	ownedHouse = null;
        }

        return name + " sold " + asset.getName() + " for $" + sellValue + " (Profit after loan: $" + (sellValue - loanToRepay) + ")";
    }
    
    public boolean hasAssetLoan() {
    	return getLoanAmount() > 0 && loanStartDay > 0;
    }
    
    public int getLoanOverdueDays(Game game) {
        if (!hasAssetLoan()) return 0;
        int currentDay = game.getClock().getDayNumber();
        return currentDay - loanStartDay;
    }
    
    public String repossessAsset() {
        int loanAmount = getLoanAmount();
        String repossessMsg = "";
        
        if (loanAmount > 2000) {
            if (ownedHouse != null) {
                repossessMsg = name + "'s house was repossessed due to overdue loan";
                ownedHouse = null;
                
                if (game != null && location.key().equalsIgnoreCase("home")) {
                	setLocation(game.location().get("street"));;
                }
            } 
            else {
                if (ownedCar != null) {
                    repossessMsg = name + "'s car was repossessed due to overdue loan";
                    ownedCar = null;
                }
            }
        } 
        else {
            if (ownedCar != null) {
                repossessMsg = name + "'s car was repossessed due to overdue loan";
                ownedCar = null;
            }
        }
        
        // Loan = 0
        bankingSystem.repayLoan(getLoanAmount());
        loanStartDay = 0;
        return "\u001B[31m[Loan Overdue] \u001B[0m" + repossessMsg;
    }
    
    public boolean isInsolvent() {
        int totalWealth = simcoin + getBankDeposit();
        int loanAmount = getLoanAmount();
        return loanAmount > totalWealth;
    }
    
    public Asset getOwnedCar() {return ownedCar;}    
    public void setOwnedCar(Car car) {this.ownedCar = car;}
    public Asset getOwnedHouse() {return ownedHouse;}    
    public void setOwnedHouse(House house) {this.ownedHouse = house;}
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
    // End: Bank
    
    
    // Start: Output message management - message only show when user active again
    private final List<String> pendingLoanMessages = new ArrayList<>();

    public void addPendingLoanMessage(String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            pendingLoanMessages.add(msg);
        }
    }

    public List<String> getAndClearPendingLoanMessages() {
        List<String> messages = new ArrayList<>(pendingLoanMessages);
        pendingLoanMessages.clear(); 
        return messages;
    }

    public void clearPendingLoanMessages() {
        pendingLoanMessages.clear();
    }
    // End: Output message management
}