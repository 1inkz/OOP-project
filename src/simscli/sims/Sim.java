package simscli.sims;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.House;
import simscli.asset.Hotel;
import simscli.bank.BankingSystem;
import simscli.game.Game;
import simscli.game.GameLogger;
import simscli.jobs.Job;
import simscli.location.Location;
import simscli.pets.Pet;
import simscli.stats.Effect;
import simscli.stats.Needs;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

/**
 * Core Sim class: orchestrates components for stats, banking, employment, assets, and pets.
 * Maintains the public API for backward compatibility while delegating to focused components.
 * Follows Single Responsibility Principle by separating concerns into discrete components.
 */
public abstract class Sim {
    private final String name;
    private final SimType type;
    private Location location;
    private int startDay;
    private Game game;
    
    // Components managing specific concerns
    private final SimStatsComponent stats;
    private final SimBankingComponent banking;
    private final SimEmploymentComponent employment;
    private final SimAssetsComponent assets;
    private final SimPetsComponent pets;
    private final GameLogger logger;
    
    // Loan messaging
    private final List<String> pendingLoanMessages = new ArrayList<>();

    public void setGame(Game game) { this.game = game; }
    public Game getGame() { return game; }

    protected Sim(String name, SimType type, Game game) {
        this(name, type, game, new ConsoleGameLogger());
    }

    protected Sim(String name, SimType type, Game game, GameLogger logger) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name required");
        this.name = name.trim();
        this.type = type;
        this.game = game;
        this.logger = logger;
        this.startDay = game.getClock().getDayNumber();
        
        // Initialize components
        this.stats = new SimStatsComponent();
        this.banking = new SimBankingComponent(300);
        this.employment = new SimEmploymentComponent();
        this.assets = new SimAssetsComponent();
        this.pets = new SimPetsComponent(this.name, logger);
    }

    // Core Identity
    public final String getName() { return name; }
    public final SimType getType() { return type; }
    public final boolean isAlive() { return stats.isAlive(); }
    public void setAlive(boolean alive) { stats.setAlive(alive); }

    /** Each subtype defines its decay pace. */
    public abstract Effect hourlyDecay();
    
    // ==================== Time Management ====================
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
        return assets.getLoanStartDay();
    }
    
    public void setLoanStartDay(int loanStartDay) {
        assets.setLoanStartDay(loanStartDay);
    }
    
    // ==================== Stats/Needs Management ====================
    public Needs getNeeds() { 
        return stats.getNeeds(); 
    }

    public final int moodScore() {
        return stats.moodScore();
    }

    public final boolean isCritical(NeedType t) {
        return stats.isCritical(t);
    }
    
    public void applyEffect(Effect effect) {
        stats.applyEffect(effect);
    }
    
    // ==================== Skills Management ====================
    public final int getSkillLevel(SkillType type) {
        return stats.getSkillLevel(type);
    }

    public final int gainSkill(SkillType type, int amount) {
        int modified = amount;
        // Apply Sim type modifiers
        switch (this.type) {
            case CHILD:
                modified = (int) Math.round(modified * 1.5);
                break;
            case ADULT:
                modified = (int) Math.round(modified * 1.0);
                break;
            case ELDER:
                modified = (int) Math.round(modified * 0.70);
                break;
    }
        return stats.gainSkill(type, modified);
    }

    public final Map<SkillType, Integer> getAllSkillLevels() {
        return stats.getAllSkillLevels();
    }

    public final void setAllSkillLevels(Map<SkillType, Integer> values) {
        stats.setAllSkillLevels(values);
    }
    
    public simscli.stats.Skills getSkills() {
        return stats.getSkills();
    }

    // ==================== Employment Management ====================
    public Map<String, Integer> getAllJobLevels() {
        return employment.getAllJobLevels();
    }

    public final String getJobName() {
        return employment.getJobName();
    }

    public Job getJob() {
        return employment.getJob();
    }
    
    public final void setJob(Job newJob) {
        employment.setJob(newJob);
    }
    
    public final int getJobLevel() {
        return employment.getJobLevel();
    }
    
    public void setJobLevel(int jobLevel) {
        employment.setJobLevel(jobLevel);
    }
    
    public void setAllJobLevels(Map<String, Integer> jobLevels) {
        employment.setAllJobLevels(jobLevels);
    }
    
    public final String work() {
        if (!isAlive()) {
            return name + " is not available.";
        }

        SimEmploymentComponent.WorkResult result = employment.work(name, this.type);
        if (!result.isSuccess()) {
            return result.getMessage();
        }

        applyEffect(result.getEffect());
        banking.earnSimcoin(result.getEarnings());

        return result.getMessage();
    }

    // ==================== Location Management ====================
    public final Location getLocation() {
        return location;
    }

    public final void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("location required");
        }
        this.location = location;
    }

    // ==================== Asset Management ====================
    public boolean buyAsset(Asset asset) {
        int currentLoanLimit = BankingSystem.getLoanLimit() - banking.getLoanAmount();
        
        SimAssetsComponent.PurchaseResult result = assets.buyAsset(asset, banking.getSimcoin(), currentLoanLimit);
        
        if (!result.isSuccess()) {
            logger.warn(result.getMessage());
            return false;
        }

        banking.earnSimcoin(result.getSimcoinChange());
        if (result.getLoanAmount() > 0) {
            banking.applyLoan(result.getLoanAmount());
            if (assets.getLoanStartDay() == 0) {
                assets.setLoanStartDay(game.getClock().getDayNumber());
            }
        }
        
        return true;
    }

    public String sellAsset(Asset asset) {
        if (asset == null) {
            return "No asset to sell!";
        }

        int sellValue = asset.sellValue();
        int loanToRepay = Math.min(banking.getLoanAmount(), sellValue);

        banking.repayLoan(loanToRepay);
        if (banking.getLoanAmount() == 0) {
            assets.setLoanStartDay(0);
        }
        
        banking.earnSimcoin(sellValue - loanToRepay);
        assets.sellAsset(asset);

        return name + " sold " + asset.getName() + " for $" + sellValue + " (Profit after loan: $" + (sellValue - loanToRepay) + ")";
    }
    
    public boolean hasAssetLoan() {
        return banking.getLoanAmount() > 0 && assets.getLoanStartDay() > 0;
    }
    
    public int getLoanOverdueDays(Game game) {
        return assets.getLoanOverdueDays(game);
    }
    
    public String repossessAsset() {
        int loanAmount = banking.getLoanAmount();
        String repossessMsg = assets.repossessAsset(loanAmount, name, location, game);
        banking.repayLoan(loanAmount);
        return repossessMsg;
    }
    
    public boolean isInsolvent() {
        return banking.isInsolvent(banking.getLoanAmount());
    }
    
    public Asset getOwnedCar() { return assets.getOwnedCar(); }    
    public void setOwnedCar(Car car) { assets.setOwnedCar(car); }

    public Asset getOwnedHouse() { return assets.getOwnedHouse(); }    
    public void setOwnedHouse(House house) { assets.setOwnedHouse(house); }

    public Asset getOwnedHotel() { return assets.getOwnedHotel(); }    
    public void setOwnedHotel(Hotel hotel) { assets.setOwnedHotel(hotel); }

    public int calculateDailyHotelIncome() {
        return assets.calculateDailyHotelIncome();
    }

    // ==================== Banking Management ====================
    public BankingSystem getBankingSystem() { 
        return banking.getBankingSystem(); 
    }
    
    public void settleBankInterest() { 
        banking.settleBankInterest(); 
    }
    
    public int getBankDeposit() { 
        return banking.getBankDeposit(); 
    }
    
    public void setBankDeposit(int v) { 
        banking.setBankDeposit(v); 
    }
    
    public final int getSimcoin() { 
        return banking.getSimcoin(); 
    }
    
    public void setSimcoin(int v) { 
        banking.setSimcoin(v); 
    }
    
    public int getLoanAmount() { 
        return banking.getLoanAmount(); 
    }
    
    public void setLoanAmount(int v) { 
        banking.applyLoan(v); 
    }

    public final void earnSimcoin(int amount) {
        banking.earnSimcoin(amount);
    }

    public boolean spendSimcoin(int amount) {
        return banking.spendSimcoin(amount);
    }

    public void addSimcoin(int amount) {
        earnSimcoin(amount);
    }

    // ==================== Pending Message Management ====================
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

    // ==================== Pet Management ====================
    public List<Pet> getPets() {
        return pets.getPets();
    }

    public void adoptPet(Pet pet) {
        pets.adoptPet(pet);
    }

    public void updatePetsHourly() {
        pets.updatePetsHourly();
    }
}