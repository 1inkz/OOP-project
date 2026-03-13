package simscli;

import simscli.asset.Car;
import simscli.asset.House;
import simscli.game.Game;
import simscli.location.Location;
import simscli.sims.*;
import simscli.stats.NeedType;
import simscli.jobs.JobFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SaveGame {
    private static final String SAVE_FILE = "savegame.txt";

    // Ensure savegame.txt exist
    public static void ensureSaveFileExists() {
        File file = new File(SAVE_FILE);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to create save file: " + e.getMessage());
        }
    }
    
    // Check if savegame.txt have valid data
    public static boolean hasValidSaveData() {
    	File file = new File(SAVE_FILE);
    	return file.exists() && file.length() > 0;
    }

    // Save Game
    public static void saveGame(Game game) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // Save GameClock state (single time source)
            GameClock clock = game.getClock();
            writer.write(clock.getDayNumber() + "|" + clock.getMinuteOfDay() + "|" + game.getActiveSimIndex());
            writer.newLine();

            // Save Sims status
            List<Sim> sims = game.sims();
            
            for (Sim sim : sims) {
            	
                // Save Job level
                Map<String, Integer> allJobLevels = sim.getAllJobLevels();
                String jobLevelsStr = "";
                for (Map.Entry<String, Integer> entry : allJobLevels.entrySet()) {
                    String jobName = entry.getKey();
                    if ("Jobless".equals(jobName)) continue; 
                    jobLevelsStr += jobName + ":" + entry.getValue() + ",";
                }
                if (jobLevelsStr.endsWith(",")) {
                    jobLevelsStr = jobLevelsStr.substring(0, jobLevelsStr.length() - 1);
                }

                writer.write(sim.getName() + "|" + sim.getType() + "|" + 
                        sim.getJobName() + "|" + sim.getJobLevel() + "|" + sim.getLocation().key() + "|" +
                		sim.getSimcoin() + "|" + sim.getBankDeposit() + "|" + sim.getLoanAmount() + "|" +
                		sim.getStartDay() + "|" + sim.getLoanStartDay() + "|" +
                		
    					(sim.getOwnedCar() != null ? 
    							sim.getOwnedCar().getId() + "-" + sim.getOwnedCar().getName().replace("|", "").replace("-", "_") + "-" + sim.getOwnedCar().getValue() : "null") + "|" +

        				(sim.getOwnedHouse() != null ? 
        						sim.getOwnedHouse().getId() + "-" + sim.getOwnedHouse().getName().replace("|", "").replace("-", "_") + "-" + sim.getOwnedHouse().getValue() : "null") + "|" +
        
                		sim.getNeeds().get(NeedType.HUNGER) + "|" + sim.getNeeds().get(NeedType.ENERGY) + "|" +
                        sim.getNeeds().get(NeedType.HYGIENE) + "|" + sim.getNeeds().get(NeedType.SOCIAL) + "|" +
                        sim.getNeeds().get(NeedType.FUN) + "|" + sim.getNeeds().get(NeedType.BLADDER) + "|" +
                        jobLevelsStr);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // Load Game 
    public static boolean loadGame(Game game) {
        ensureSaveFileExists();
        
        File file = new File(SAVE_FILE);
        if (file.length() == 0) {
        	return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Load GameClock state
            String[] timeParts = reader.readLine().split("\\|");
            GameClock loadedClock = new GameClock(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
            game.setClock(loadedClock);
            int activeSimIndex = Integer.parseInt(timeParts[2]);
            game.clearSimList();

            // Load Sims
            String line;
            while ((line = reader.readLine()) != null) {
            	if (line.trim().isEmpty()) continue;
            	
                String[] parts = line.split("\\|");
                Sim sim = switch (SimType.valueOf(parts[1])) {
                    case CHILD -> new ChildSim(parts[0], game);
                    case ADULT -> new AdultSim(parts[0], game);
                    case ELDER -> new ElderSim(parts[0], game);
                };

                sim.setJob(JobFactory.create(parts[2]));
                sim.setJobLevel(Integer.parseInt(parts[3]));
                sim.setLocation(game.location().get(parts[4]));
                
                sim.setSimcoin(Integer.parseInt(parts[5]));
                sim.setBankDeposit(Integer.parseInt(parts[6]));
                sim.setLoanAmount(Integer.parseInt(parts[7]));
                
                sim.setStartDay(Integer.parseInt(parts[8]));
                sim.setLoanStartDay(Integer.parseInt(parts[9]));
                
                // Load Car 
                String carData = parts[10];
                if (!carData.equals("null")) {
                    String[] carParts = carData.split("-"); 
                    if (carParts.length == 3) { 
                        int carId = Integer.parseInt(carParts[0]);
                        String carName = carParts[1].replace("_", "-"); 
                        int carValue = Integer.parseInt(carParts[2]);
                        sim.setOwnedCar(new Car(carId, carName, carValue, 0.0));
                    } else {
                        sim.setOwnedCar(null);
                    }
                } else {
                    sim.setOwnedCar(null);
                }
                
                // Load House 
                String houseData = parts[11];
                if (!houseData.equals("null")) {
                    String[] houseParts = houseData.split("-");
                    if (houseParts.length == 3) {
                        int houseId = Integer.parseInt(houseParts[0]);
                        String houseName = houseParts[1].replace("_", "-");
                        int houseValue = Integer.parseInt(houseParts[2]);
                        sim.setOwnedHouse(new House(houseId, houseName, houseValue));
                    } else {
                        sim.setOwnedHouse(null);
                    }
                } else {
                    sim.setOwnedHouse(null);
                }
                
                // Restore needs (single add method)
                sim.getNeeds().add(NeedType.HUNGER, Integer.parseInt(parts[12]) - sim.getNeeds().get(NeedType.HUNGER));
                sim.getNeeds().add(NeedType.ENERGY, Integer.parseInt(parts[13]) - sim.getNeeds().get(NeedType.ENERGY));
                sim.getNeeds().add(NeedType.HYGIENE, Integer.parseInt(parts[14]) - sim.getNeeds().get(NeedType.HYGIENE));
                sim.getNeeds().add(NeedType.SOCIAL, Integer.parseInt(parts[15]) - sim.getNeeds().get(NeedType.SOCIAL));
                sim.getNeeds().add(NeedType.FUN, Integer.parseInt(parts[16]) - sim.getNeeds().get(NeedType.FUN));
                sim.getNeeds().add(NeedType.BLADDER, Integer.parseInt(parts[17]) - sim.getNeeds().get(NeedType.BLADDER));
                
                // Load Job
                Map<String, Integer> jobLevelsMap = new HashMap<>();
                if (parts.length >= 19) { 
                    String jobLevelsStr = parts[18];
                    if (jobLevelsStr != null && !jobLevelsStr.trim().isEmpty()) {
                        String[] jobLevelPairs = jobLevelsStr.split(",");
                        for (String pair : jobLevelPairs) {
                            String[] keyValue = pair.split(":");
                            if (keyValue.length == 2) {
                                String jobName = keyValue[0].trim();
                                int level = Integer.parseInt(keyValue[1].trim());
                                jobLevelsMap.put(jobName, level);
                            }
                        }
                    }
                }
                sim.setAllJobLevels(jobLevelsMap);
                if (!jobLevelsMap.containsKey("Jobless")) {
                	jobLevelsMap.put("Jobless", 1);
                }
                game.addSim(sim);
            }
            
            if (activeSimIndex >= 0 && activeSimIndex < game.sims().size()) {
                game.setActiveSim(activeSimIndex);
            } else {
                if (!game.sims().isEmpty()) {
                    game.setActiveSim(0);
                }
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
            return false;
        }
    }

    // Reset all
    public static void clearSaveFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write("");
        } catch (IOException e) {
            System.out.println("Clear save failed: " + e.getMessage());
        }
    }
}