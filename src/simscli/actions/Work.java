package simscli.actions;

import simscli.game.GameContext;
import simscli.jobs.Job;
import simscli.sims.Sim;
import simscli.stats.NeedType;

public final class Work implements Action {
	
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String RESET = "\u001B[0m";
	
    private static final int ENERGY_COST = 30;    
    private static final int HYGIENE_COST = 15;   
    private static final int FUN_COST = 10;
    
    @Override public String name() { return "Work"; }

    @Override
    public String perform(Sim sim, GameContext ctx) {
        if (!sim.isAlive()) {
            return RED + sim.getName() + " is dead and cannot work!" + RESET;
        }
        
        Job job = sim.getJob();
        if (!job.canWork()) {
            return RED + sim.getName() + " is jobless and cannot work!" + RESET;
        }

        int currentEnergy = sim.getNeeds().get(NeedType.ENERGY);
        if (currentEnergy < ENERGY_COST) {
            return RED + sim.getName() + " is too tired to work! (Need at least " + ENERGY_COST + " energy)" + RESET;
        }

        sim.getNeeds().add(NeedType.ENERGY, -ENERGY_COST);
        sim.getNeeds().add(NeedType.HYGIENE, -HYGIENE_COST);
        sim.getNeeds().add(NeedType.FUN, -FUN_COST);

        int jobLevel = sim.getJobLevel();
        double salary = job.salary(jobLevel);
        sim.earnSimcoin((int) salary);

        sim.setJobLevel(sim.getJobLevel() + 1);
        int newJobLevel = sim.getJobLevel();
        String levelUpMsg = "";
        if (newJobLevel > jobLevel) {
            levelUpMsg = GREEN + "Promoted to Level " + newJobLevel + "!" + RESET;
        }

        return GREEN + sim.getName() + " worked as a " + job.name() + " and earned " 
                + (int) salary + " Simcoin!" + RESET 
                + levelUpMsg ;
    }


}