package simscli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simscli.asset.Car;
import simscli.asset.Hotel;
import simscli.asset.House;
import simscli.game.Game;
import simscli.game.GameClock;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;

/**
 * Asset gameplay tests for car maintenance/travel, house comfort, and hotel economy.
 */
public class AssetGameplayTest {

    @Test
    public void carShouldReduceTravelPenaltyWhenMaintained() {
        Game noCarGame = new Game();
        Sim noCar = noCarGame.createSim("NoCar", SimType.ADULT);
        noCarGame.setActiveSim(0);
        noCar.getNeeds().set(NeedType.HUNGER, 100);
        noCar.getNeeds().set(NeedType.ENERGY, 100);
        noCarGame.travelTo("park");
        int noCarHungerDrop = 100 - noCar.getNeeds().get(NeedType.HUNGER);
        int noCarEnergyDrop = 100 - noCar.getNeeds().get(NeedType.ENERGY);

        Game carGame = new Game();
        Sim withCar = carGame.createSim("WithCar", SimType.ADULT);
        carGame.setActiveSim(0);
        withCar.setOwnedCar(new Car(1, "TestCar", 2000, 0.4));
        withCar.getNeeds().set(NeedType.HUNGER, 100);
        withCar.getNeeds().set(NeedType.ENERGY, 100);
        carGame.travelTo("park");
        int carHungerDrop = 100 - withCar.getNeeds().get(NeedType.HUNGER);
        int carEnergyDrop = 100 - withCar.getNeeds().get(NeedType.ENERGY);

        assertTrue(carHungerDrop < noCarHungerDrop);
        assertTrue(carEnergyDrop < noCarEnergyDrop);

        noCarGame.shutdown();
        carGame.shutdown();
    }

    @Test
    public void unpaidCarMaintenanceShouldDisableTravelBonusForDay() {
        Game game = new Game();
        Sim sim = game.createSim("Ava", SimType.ADULT);
        game.setActiveSim(0);

        sim.setOwnedCar(new Car(1, "Car", 2000, 0.4));
        sim.setSimcoin(0);
        sim.getBankingSystem().withdraw(sim.getBankDeposit()); // set deposit to 0

        game.setClock(new GameClock(1, 1380)); // 23:00
        game.advanceTimeForAction(); // cross day boundary and trigger daily maintenance

        sim.getNeeds().set(NeedType.HUNGER, 100);
        sim.getNeeds().set(NeedType.ENERGY, 100);
        game.travelTo("park");

        assertEquals(90, sim.getNeeds().get(NeedType.HUNGER));
        assertEquals(90, sim.getNeeds().get(NeedType.ENERGY));

        game.shutdown();
    }

    @Test
    public void houseShouldProvideComfortBonusWhileAtHome() {
        Game game = new Game();
        Sim sim = game.createSim("Mia", SimType.ADULT);
        game.setActiveSim(0);

        sim.setOwnedHouse(new House(1, "Condo", 4000));
        sim.setLocation(game.location().get("home"));
        game.setClock(new GameClock(1, 720)); // 12:00

        sim.getNeeds().set(NeedType.ENERGY, 50);
        sim.getNeeds().set(NeedType.HYGIENE, 50);
        sim.getNeeds().set(NeedType.FUN, 50);

        game.advanceTimeForAction();

        assertEquals(49, sim.getNeeds().get(NeedType.ENERGY));
        assertEquals(49, sim.getNeeds().get(NeedType.HYGIENE));
        assertEquals(49, sim.getNeeds().get(NeedType.FUN));

        game.shutdown();
    }

    @Test
    public void hotelIncomeShouldVaryByEconomyAndIncreaseAfterUpgrade() {
        Game game = new Game();
        Sim sim = game.createSim("Noah", SimType.ADULT);
        game.setActiveSim(0);

        sim.setOwnedHotel(new Hotel(1, "Intercontinental", 7000));
        int boomIncomeLevel1 = sim.calculateDailyHotelIncome(4);
        int slumpIncomeLevel1 = sim.calculateDailyHotelIncome(6);

        assertTrue(boomIncomeLevel1 > slumpIncomeLevel1);

        sim.setSimcoin(10000);
        assertTrue(sim.upgradeOwnedHotel());

        int boomIncomeLevel2 = sim.calculateDailyHotelIncome(4);
        assertTrue(boomIncomeLevel2 > boomIncomeLevel1);

        game.shutdown();
    }

    @Test
    public void dayRolloverShouldApplyHotelIncome() {
        Game game = new Game();
        Sim sim = game.createSim("Lia", SimType.ADULT);
        game.setActiveSim(0);

        sim.setOwnedHotel(new Hotel(1, "Intercontinental", 7000));
        sim.setSimcoin(0);

        game.setClock(new GameClock(3, 1380)); // day3 23:00, next day is 4 (BOOM)
        game.advanceTimeForAction();

        assertEquals(300, sim.getSimcoin());

        game.shutdown();
    }
}
