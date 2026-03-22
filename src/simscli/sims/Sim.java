package simscli.sims;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.House;
import simscli.asset.Hotel;
import simscli.bank.BankingSystem;
import simscli.game.ConsoleGameLogger;
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
    private static final int DEFAULT_STARTING_SIMCOIN = 500;

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

    /**
     * Associates this Sim with the Game instance.
     * @param game the Game orchestrator
     */
    public void setGame(Game game) { this.game = game; }
    
    /**
     * Gets the Game instance this Sim belongs to.
     * @return the Game orchestrator
     */
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
        this.banking = new SimBankingComponent(DEFAULT_STARTING_SIMCOIN);
        this.employment = new SimEmploymentComponent();
        this.assets = new SimAssetsComponent();
        this.pets = new SimPetsComponent(this.name, logger);
    }

    // Core Identity
    /**
     * Gets this Sim's name.
     * @return the Sim's name
     */
    public final String getName() { return name; }
    
    /**
     * Gets this Sim's type (Adult, Child, or Elder).
     * @return the Sim's type
     */
    public final SimType getType() { return type; }
    
    /**
     * Checks if this Sim is still alive.
     * @return true if alive, false otherwise
     */
    public final boolean isAlive() { return stats.isAlive(); }
    
    /**
     * Sets this Sim's alive status.
     * @param alive true to mark as alive, false for dead
     */
    public void setAlive(boolean alive) { stats.setAlive(alive); }

    /**
     * Returns the hourly stat decay effect for this Sim type.
     * Each subtype defines its own decay rates for needs.
     * @return Effect representing hourly need degradation
     */
    public abstract Effect hourlyDecay();
    
    // ==================== Time Management ====================
    /**
     * Gets how many in-game days this Sim has existed.
     * @param game the Game instance to check current day
     * @return the number of days since this Sim was created
     */
    public int getPersonalDay(Game game) {
        if (game == null || game.getClock() == null) {
            return 1;
        }
        int currentGameDay = game.getClock().getDayNumber();
        int personalDay = currentGameDay - this.startDay + 1;
        return Math.max(1, personalDay);
    }
    
    /**
     * Gets the day this Sim was created.
     * @return the starting day number
     */
    public int getStartDay() {
        return this.startDay;
    }
    
    /**
     * Sets the day this Sim was created.
     * @param startDay the starting day number (must be positive)
     */
    public void setStartDay(int startDay) {
        this.startDay = Math.max(1, startDay);
    }
    
    /**
     * Gets the day when an asset loan was taken out.
     * @return the loan start day or 0 if no loan
     */
    public int getLoanStartDay() {
        return assets.getLoanStartDay();
    }
    
    /**
     * Sets the day when an asset loan was taken out.
     * @param loanStartDay the loan start day
     */
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

    /**
     * Consumes one-time trigger when a need reaches zero.
     */
    public boolean consumeZeroNeedTrigger(NeedType type) {
        return stats.consumeZeroNeedTrigger(type);
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
    /**
     * Gets the current location.
     * @return the Location where this Sim is
     */
    public final Location getLocation() {
        return location;
    }

    /**
     * Sets the current location.
     * @param location the Location to move to (non-null)
     * @throws IllegalArgumentException if location is null
     */
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

    public int calculateDailyHotelIncome(int dayNumber) {
        return assets.calculateDailyHotelIncome(dayNumber);
    }

    public double getTravelFatigueMultiplier() {
        return assets.getTravelFatigueMultiplier();
    }

    public boolean isCarMaintenancePaid() {
        return assets.isCarMaintenancePaid();
    }

    public void setCarMaintenancePaid(boolean paid) {
        assets.setCarMaintenancePaid(paid);
    }

    public int getOwnedHotelLevel() {
        return assets.getOwnedHotelLevel();
    }

    public int getOwnedHotelUpgradeCost() {
        return assets.getOwnedHotelUpgradeCost();
    }

    public boolean upgradeOwnedHotel() {
        int upgradeCost = assets.getOwnedHotelUpgradeCost();
        if (upgradeCost <= 0) {
            return false;
        }

        if (!spendSimcoin(upgradeCost)) {
            return false;
        }
        return assets.upgradeOwnedHotel();
    }

    /**
     * Applies day-end asset economy flow and returns summary text for UI/logs.
     */
    public String processDailyAssetEconomy(int dayNumber) {
        StringBuilder summary = new StringBuilder();

        int carCost = assets.getDailyCarMaintenanceCost();
        if (carCost > 0) {
            boolean paid = spendSimcoin(carCost);
            if (!paid && getBankingSystem().withdraw(carCost)) {
                earnSimcoin(carCost);
                paid = spendSimcoin(carCost);
            }
            assets.setCarMaintenancePaid(paid);

            if (paid) {
                summary.append("Car maintenance paid: $").append(carCost).append(". ");
            } else {
                summary.append("Car maintenance overdue; travel bonus disabled today. ");
            }
        }

        int hotelIncome = calculateDailyHotelIncome(dayNumber);
        if (hotelIncome > 0) {
            earnSimcoin(hotelIncome);
            summary.append("Hotel income +$").append(hotelIncome).append(" (Lvl ")
                    .append(getOwnedHotelLevel()).append("). ");
        }

        if (summary.length() == 0) {
            return "";
        }
        return summary.toString().trim();
    }

    public void applyHouseComfortBonus() {
        if (getOwnedHouse() == null) {
            return;
        }
        if (getLocation() == null || !"home".equalsIgnoreCase(getLocation().key())) {
            return;
        }

        applyEffect(Effect.none()
                .plus(NeedType.ENERGY, +2)
                .plus(NeedType.HYGIENE, +1)
                .plus(NeedType.FUN, +1));
    }

    // ==================== Banking Management ====================
    /**
     * Gets the banking system.
     * @return the BankingSystem instance
     */
    public BankingSystem getBankingSystem() { 
        return banking.getBankingSystem(); 
    }
    
    /**
     * Applies bank interest to deposits.
     */
    public void settleBankInterest() { 
        banking.settleBankInterest(); 
    }
    
    /**
     * Gets the bank deposit amount.
     * @return current bank deposit
     */
    public int getBankDeposit() { 
        return banking.getBankDeposit(); 
    }
    
    /**
     * Sets the bank deposit amount.
     * @param v the new deposit amount
     */
    public void setBankDeposit(int v) { 
        banking.setBankDeposit(v); 
    }
    
    /**
     * Gets the current Simcoin (cash) amount.
     * @return cash on hand
     */
    public final int getSimcoin() { 
        return banking.getSimcoin(); 
    }
    
    /**
     * Sets the cash amount.
     * @param v the new cash amount
     */
    public void setSimcoin(int v) { 
        banking.setSimcoin(v); 
    }
    
    /**
     * Gets the outstanding loan amount.
     * @return loan amount or 0 if no loan
     */
    public int getLoanAmount() { 
        return banking.getLoanAmount(); 
    }
    
    /**
     * Sets the loan amount.
     * @param v the new loan amount
     */
    public void setLoanAmount(int v) { 
        banking.applyLoan(v); 
    }

    /**
     * Earns money (adds to cash).
     * @param amount the amount to earn
     */
    public final void earnSimcoin(int amount) {
        banking.earnSimcoin(amount);
    }

    /**
     * Spends money from cash.
     * @param amount the amount to spend
     * @return true if sufficient funds, false otherwise
     */
    public boolean spendSimcoin(int amount) {
        return banking.spendSimcoin(amount);
    }

    /**
     * Adds money (alias for earnSimcoin).
     * @param amount the amount to add
     */
    public void addSimcoin(int amount) {
        earnSimcoin(amount);
    }

    // ==================== Pending Message Management ====================
    /**
     * Adds a pending loan message to notify the Sim later.
     * @param msg the message to queue
     */
    public void addPendingLoanMessage(String msg) {
        if (msg != null && !msg.trim().isEmpty()) {
            pendingLoanMessages.add(msg);
        }
    }

    /**
     * Retrieves and clears all pending loan messages.
     * @return list of pending messages to display
     */
    public List<String> getAndClearPendingLoanMessages() {
        List<String> messages = new ArrayList<>(pendingLoanMessages);
        pendingLoanMessages.clear(); 
        return messages;
    }

    /**
     * Clears all pending loan messages without retrieving them.
     */
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

//	public abstract String performWork();
//	public abstract void eatMeal();
//	public abstract void sleep();
//	public abstract void shower();
}