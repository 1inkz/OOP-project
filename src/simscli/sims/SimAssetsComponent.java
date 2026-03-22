package simscli.sims;

import simscli.asset.Asset;
import simscli.asset.Car;
import simscli.asset.CyclicalEconomyPolicy;
import simscli.asset.EconomyPolicy;
import simscli.asset.EconomyState;
import simscli.asset.House;
import simscli.asset.Hotel;
import simscli.game.Game;
import simscli.location.Location;

/**
 * Manages Sim assets: cars, houses, hotels and repossession logic.
 * Encapsulates asset ownership and related rules.
 */
public class SimAssetsComponent {
    private static final EconomyPolicy ECONOMY_POLICY = new CyclicalEconomyPolicy();

    private Asset ownedCar;
    private Asset ownedHouse;
    private Asset ownedHotel;
    private boolean carMaintenancePaid = true;
    private int loanStartDay = 0;

    public SimAssetsComponent() {
    }

    // Assets
    // Car
    public Asset getOwnedCar() {
        return ownedCar;
    }

    public void setOwnedCar(Car car) {
        this.ownedCar = car;
        this.carMaintenancePaid = car != null;
    }

    // House
    public Asset getOwnedHouse() {
        return ownedHouse;
    }

    public void setOwnedHouse(House house) {
        this.ownedHouse = house;
    }

    // Hotel
    public Asset getOwnedHotel() {
        return ownedHotel;
    }

    public void setOwnedHotel(Hotel hotel) {
        this.ownedHotel = hotel;
    }

    public int calculateDailyHotelIncome() {
        return calculateDailyHotelIncome(1);
    }

    public int calculateDailyHotelIncome(int dayNumber) {
        if (ownedHotel instanceof Hotel hotel) {
            EconomyState state = ECONOMY_POLICY.stateForDay(dayNumber);
            return hotel.calculateDailyIncome(state);
        }
        return 0;
    }

    public int getDailyCarMaintenanceCost() {
        if (ownedCar instanceof Car car) {
            return car.getDailyMaintenanceCost();
        }
        return 0;
    }

    public void setCarMaintenancePaid(boolean paid) {
        this.carMaintenancePaid = paid;
    }

    public boolean isCarMaintenancePaid() {
        return carMaintenancePaid;
    }

    public double getTravelFatigueMultiplier() {
        if (!(ownedCar instanceof Car car)) {
            return 1.0;
        }
        if (!carMaintenancePaid) {
            return 1.0;
        }
        return car.getTravelMultiplier();
    }

    public int getOwnedHotelLevel() {
        if (ownedHotel instanceof Hotel hotel) {
            return hotel.getLevel();
        }
        return 0;
    }

    public int getOwnedHotelUpgradeCost() {
        if (ownedHotel instanceof Hotel hotel) {
            return hotel.getUpgradeCost();
        }
        return 0;
    }

    public boolean upgradeOwnedHotel() {
        if (ownedHotel instanceof Hotel hotel) {
            return hotel.upgrade();
        }
        return false;
    }

    // Loan timing
    public int getLoanStartDay() {
        return loanStartDay;
    }

    public void setLoanStartDay(int day) {
        this.loanStartDay = day;
    }

    public boolean hasAssetLoan() {
        return loanStartDay > 0;
    }

    public int getLoanOverdueDays(Game game) {
        if (!hasAssetLoan()) return 0;
        int currentDay = game.getClock().getDayNumber();
        return currentDay - loanStartDay;
    }

    /**
     * Attempts to buy an asset with payment plan.
     * Returns purchase result with message and amount financed.
     */
    public PurchaseResult buyAsset(Asset asset, int currentSimcoin, int loanLimit) {
        if (asset.isCar() && ownedCar != null) {
            return new PurchaseResult(false, "You already own a car!");
        }

        if (asset.isHouse() && ownedHouse != null) {
            return new PurchaseResult(false, "You already own a house!");
        }

        if (asset.isHotel() && ownedHotel != null) {
            return new PurchaseResult(false, "You already own a hotel!");
        }

        int totalCost = asset.getValue();
        int downPayment = asset.isCar() ? 400 : 1500;
        int loanAmount = totalCost - downPayment;
        if (loanAmount < 0) loanAmount = 0;

        if (currentSimcoin >= totalCost) {
            storeAsset(asset);
            return new PurchaseResult(true, -totalCost, 0);
        }

        if (currentSimcoin < downPayment) {
            return new PurchaseResult(false, "Insufficient Simcoin for down payment!");
        }

        if (loanAmount > loanLimit) {
            return new PurchaseResult(false, "Loan limit exceeded");
        }

        storeAsset(asset);
        return new PurchaseResult(true, -downPayment, loanAmount);
    }

    private void storeAsset(Asset asset) {
        if (asset.isCar()) {
            ownedCar = asset;
            carMaintenancePaid = true;
        } else if (asset.isHouse()) {
            ownedHouse = asset;
        } else if (asset.isHotel()) {
            ownedHotel = asset;
        }
    }

    /**
     * Sells an asset and removes ownership.
     */
    public void sellAsset(Asset asset) {
        if (asset.isCar()) {
            ownedCar = null;
            carMaintenancePaid = true;
        } else if (asset.isHouse()) {
            ownedHouse = null;
        } else if (asset.isHotel()) {
            ownedHotel = null;
        }
    }

    /**
     * Repossesses assets due to loan default.
     * Returns repossession message and marks loan as cleared.
     */
    public String repossessAsset(int loanAmount, String simName, Location currentLocation, Game game) {
        String repossessMsg = "";

        if (loanAmount > 2000) {
            if (ownedHouse != null) {
                repossessMsg = simName + "'s house was repossessed due to overdue loan";
                ownedHouse = null;

                if (game != null && currentLocation.key().equalsIgnoreCase("home")) {
                    currentLocation = game.location().get("street");
                }
            } else if (ownedCar != null) {
                repossessMsg = simName + "'s car was repossessed due to overdue loan";
                ownedCar = null;
            }
        } else {
            if (ownedCar != null) {
                repossessMsg = simName + "'s car was repossessed due to overdue loan";
                ownedCar = null;
            }
        }

        loanStartDay = 0;
        return "\u001B[31m[Loan Overdue] \u001B[0m" + repossessMsg;
    }

    // Purchase result DTO
    public static class PurchaseResult {
        private final boolean success;
        private final String message;
        private final int simcoinChange;
        private final int loanAmount;

        public PurchaseResult(boolean success, String message) {
            this(success, message, 0, 0);
        }

        public PurchaseResult(boolean success, int simcoinChange, int loanAmount) {
            this.success = success;
            this.message = "";
            this.simcoinChange = simcoinChange;
            this.loanAmount = loanAmount;
        }

        public PurchaseResult(boolean success, String message, int simcoinChange, int loanAmount) {
            this.success = success;
            this.message = message;
            this.simcoinChange = simcoinChange;
            this.loanAmount = loanAmount;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getSimcoinChange() { return simcoinChange; }
        public int getLoanAmount() { return loanAmount; }
    }
}