package simscli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import simscli.bank.BankingSystem;
import simscli.game.ConsoleGameLogger;
import simscli.game.Game;
import simscli.game.SimManager;
import simscli.jobs.Job; // 需确保Job类可访问
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;
import simscli.stats.SkillType; // 需确保SkillType可访问

// 注意：需补充SaveGame类的导入（根据你的实际包路径）
import simscli.SaveGame;

public class SimulationIntegrationTest {
    private Game game;
    private Sim activeSim;
    private BankingSystem bankingSystem;
    private SimManager simManager;

    /**
     * Testing Sim should have full state
     */
    @Before
    public void setUp() {
        game = new Game(new ConsoleGameLogger());
        game.location().values().forEach(loc -> {}); 
        SaveGame.ensureSaveFileExists(); 
        
        simManager = new SimManager(game.getLogger());
        activeSim = game.createSim("Charlie", SimType.ADULT); 
        
        initSimForTests(activeSim);
        
        bankingSystem = new BankingSystem(); 
        resetBankingSystem(bankingSystem);
        
        //game.setActiveSim(0);
        //simManager.setActiveSim(0);
    }

    /**
     * Make sure testing Sim can work and have skill
     */
    private void initSimForTests(Sim sim) {
        sim.getNeeds().set(NeedType.HUNGER, 50);
        sim.getNeeds().set(NeedType.ENERGY, 50);
        sim.getNeeds().set(NeedType.HYGIENE, 50);
        sim.getNeeds().set(NeedType.FUN, 50);
        
        Job testJob = createTestJob();
        sim.setJob(testJob);
        sim.setJobLevel(1); 
        
        Map<SkillType, Integer> skillLevels = new HashMap<>();
        skillLevels.put(SkillType.WORK_ETHIC, 10);
        for (SkillType skill : testJob.primarySkills()) {
            skillLevels.put(skill, 10);
        }
        sim.setAllSkillLevels(skillLevels);
        
        sim.setAlive(true);
    }

    /**
     * Create a job for new Sims
     */
    private Job createTestJob() {
        return new Job() {
            @Override
            public String name() {
                return "Test Job";
            }

            @Override
            public boolean canWork() {
                return true; 
            }

            @Override
            public double salary(int level) {
                return 300; 
            }

            @Override
            public SkillType[] primarySkills() {
                return new SkillType[]{SkillType.WORK_ETHIC}; 
            }

			@Override
			public String[] getWorkLocations() {
				return new String[] { "park" };
			}
        };
    }

    /**
     * Reseting bank
     */
    private void resetBankingSystem(BankingSystem bs) {
        if (bs.getDeposit() > 0) {
            bs.withdraw(bs.getDeposit()); 
        }
        if (bs.getLoanAmount() > 0) {
            bs.repayLoan(bs.getLoanAmount()); 
        }
    }

    // ===================== Test Case =====================
    @Test
    public void testCoreObjectInitialization() {
        assertNotNull(game);
        assertNotNull(activeSim);
        assertNotNull(bankingSystem);
        
        assertEquals("Charlie", activeSim.getName());
        assertEquals(SimType.ADULT, activeSim.getType());
        assertTrue(activeSim.isAlive());
        assertNotNull(activeSim.getJob());
    }
    
    @Test
    public void fullWorkflow_workDepositEarnInterestWithdraw_shouldWorkCorrectly() {
        String workResult = activeSim.work();
        assertTrue(workResult.contains("earned"));
        int afterWorkSimcoin = activeSim.getSimcoin();
        assertEquals(600, afterWorkSimcoin);  // Initial=300 + earn300

        int depositAmount = afterWorkSimcoin;
        boolean depositSuccess = bankingSystem.deposit(depositAmount);
        activeSim.spendSimcoin(depositAmount);
        assertTrue(depositSuccess);
        assertEquals(0, activeSim.getSimcoin());  // Simcoin = 0
        assertEquals(depositAmount, bankingSystem.getDeposit());  // Deposit = 600
        
        bankingSystem.settleInterest();
        int interest = (int) (depositAmount * 0.0005);
        assertEquals(depositAmount + interest, bankingSystem.getDeposit()); // Deposit = 600
        
        int withdrawAmount = bankingSystem.getDeposit();
        boolean withdrawSuccess = bankingSystem.withdraw(withdrawAmount);  
        activeSim.earnSimcoin(withdrawAmount);
        assertTrue(withdrawSuccess);
        assertEquals(withdrawAmount, activeSim.getSimcoin());  // Simcoin = 600
        assertEquals(0, bankingSystem.getDeposit());  // Deposit = 0
    }

    @Test
    public void loanWorkflow_applyLoanSpendRepay_shouldWorkCorrectly() {
        boolean loanSuccess = bankingSystem.applyLoan(1000);
        activeSim.earnSimcoin(1000);
        assertTrue(loanSuccess);
        assertEquals(1000, bankingSystem.getLoanAmount());
        assertEquals(1300, activeSim.getSimcoin());  // Initial300 + Loan1000
       
        boolean spendSuccess = activeSim.spendSimcoin(500);
        assertTrue(spendSuccess);
        assertEquals(800, activeSim.getSimcoin());

        boolean repaySuccess = activeSim.spendSimcoin(800);
        bankingSystem.repayLoan(800);
        assertTrue(repaySuccess);
        assertEquals(200, bankingSystem.getLoanAmount());
        assertEquals(0, activeSim.getSimcoin());
    }

    @Test
    public void needActions_eatSleepShower_shouldUpdateNeeds() {
        int initialHunger = activeSim.getNeeds().get(NeedType.HUNGER);
        int initialEnergy = activeSim.getNeeds().get(NeedType.ENERGY);
        int initialHygiene = activeSim.getNeeds().get(NeedType.HYGIENE);
        
        game.travelTo("park");

        game.performLocationAction(5); // clean in public
        assertTrue(activeSim.getNeeds().get(NeedType.HYGIENE) > initialHygiene);
        
        game.performLocationAction(2); // eat snack
        assertTrue(activeSim.getNeeds().get(NeedType.HUNGER) > initialHunger);  // hunger level increase after eat

        game.performLocationAction(4); // sleep       
        assertEquals(90, activeSim.getNeeds().get(NeedType.ENERGY)); // sleep will increase energy till 90
    }

    @Test
    public void timeAdvance_1Hour_shouldDecayNeeds() {
        int initialHunger = activeSim.getNeeds().get(NeedType.HUNGER);
        int initialEnergy = activeSim.getNeeds().get(NeedType.ENERGY);
        int initialHygiene = activeSim.getNeeds().get(NeedType.HYGIENE);
        
        game.advanceTimeForAction();
        
        int newHunger = activeSim.getNeeds().get(NeedType.HUNGER);
        int newEnergy = activeSim.getNeeds().get(NeedType.ENERGY);
        int newHygiene = activeSim.getNeeds().get(NeedType.HYGIENE);
        
        assertTrue(newHunger >= initialHunger - 4 && newHunger <= initialHunger - 2);
        assertTrue(newEnergy >= initialEnergy - 4 && newEnergy <= initialEnergy - 2);
        assertTrue(newHygiene >= initialHygiene - 3 && newHygiene <= initialHygiene - 1);
    }
}