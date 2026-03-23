package simscli.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import simscli.asset.Car;
import simscli.asset.Hotel;
import simscli.asset.House;
import simscli.game.Game;
import simscli.game.GameClock;
import simscli.jobs.JobFactory;
import simscli.pets.Pet;
import simscli.sims.AdultSim;
import simscli.sims.ChildSim;
import simscli.sims.ElderSim;
import simscli.sims.Sim;
import simscli.sims.SimType;
import simscli.stats.NeedType;
import simscli.stats.SkillType;

/**
 * File-system backed persistence adapter for savegame.txt.
 */
public final class FileSaveRepository implements SaveRepository {
    private static final String SAVE_FILE = "savegame.txt";

    @Override
    public void ensureSaveFileExists() {
        File file = new File(SAVE_FILE);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to create save file: " + e.getMessage());
        }
    }

    @Override
    public boolean hasValidSaveData() {
        File file = new File(SAVE_FILE);
        if (!file.exists() || file.length() == 0) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            // Only if second line not empty consider have data as first line is date time string
            String secondLine = reader.readLine();
            return secondLine != null && !secondLine.trim().isEmpty();
        } catch (IOException e) {
            System.out.println("Error checking save file validity: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void saveGame(Game game) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            List<Sim> sims = game.sims();

            int originalActiveIndex = game.getActiveSimIndex();
            int savedActiveIndex = -1;
            int savedIndexCounter = 0;
            //int activeIndex = game.getActiveSimIndex();

            for (int i = 0; i < sims.size(); i++) {
                Sim sim = sims.get(i);
                if (!sim.isAlive()) continue;

                if (i == originalActiveIndex) {
                    savedActiveIndex = savedIndexCounter;
                }
                savedIndexCounter++;

            }

            GameClock clock = game.getClock();
            writer.write(clock.getDayNumber() + "|" + clock.getMinuteOfDay() + "|" + savedActiveIndex);
            writer.newLine();

            for (Sim sim : sims) {
                if (!sim.isAlive()) continue;

                Map<String, Integer> allJobLevels = sim.getAllJobLevels();
                StringBuilder jobLevelsStr = new StringBuilder();

                for (Map.Entry<String, Integer> entry : allJobLevels.entrySet()) {
                    String jobName = entry.getKey();
                    if ("Jobless".equals(jobName)) continue;
                    if (jobLevelsStr.length() > 0) jobLevelsStr.append(",");
                    jobLevelsStr.append(jobName).append(":").append(entry.getValue());
                }

                StringBuilder skillLevelsStr = new StringBuilder();
                for (SkillType skillType : SkillType.values()) {
                    if (skillLevelsStr.length() > 0) skillLevelsStr.append(",");
                    skillLevelsStr.append(skillType.name())
                                  .append(":")
                                  .append(sim.getSkillLevel(skillType));
                }

                // Serialize pets
                StringBuilder petsStr = new StringBuilder();
                for (Pet pet : sim.getPets()) {
                    if (petsStr.length() > 0) petsStr.append(",");
                    petsStr.append(pet.serialize());
                    //System.out.println("[SaveGame] Saving pet: " + pet.getName() + " for Sim: " + sim.getName());
                }
                if (petsStr.length() == 0) {
                    //System.out.println("[SaveGame] No pets to save for Sim: " + sim.getName());
                }

                writer.write(
                    sim.getName() + "|" +
                    sim.getType() + "|" +
                    sim.getJobName() + "|" +
                    sim.getJobLevel() + "|" +
                    sim.getLocation().key() + "|" +
                    sim.getSimcoin() + "|" +
                    sim.getBankDeposit() + "|" +
                    sim.getLoanAmount() + "|" +
                    sim.getStartDay() + "|" +
                    sim.getLoanStartDay() + "|" +
                    (sim.getOwnedCar() != null
                        ? sim.getOwnedCar().getId() + "-" +
                          sim.getOwnedCar().getName().replace("|", "").replace("-", "_") + "-" +
                          sim.getOwnedCar().getValue()
                        : "null") + "|" +
                    (sim.getOwnedHouse() != null
                        ? sim.getOwnedHouse().getId() + "-" +
                          sim.getOwnedHouse().getName().replace("|", "").replace("-", "_") + "-" +
                          sim.getOwnedHouse().getValue()
                        : "null") + "|" +
                    (sim.getOwnedHotel() != null
                        ? sim.getOwnedHotel().getId() + "-" +
                          sim.getOwnedHotel().getName().replace("|", "").replace("-", "_") + "-" +
                          sim.getOwnedHotel().getValue()
                        : "null") + "|" +
                    sim.getNeeds().get(NeedType.HUNGER) + "|" +
                    sim.getNeeds().get(NeedType.ENERGY) + "|" +
                    sim.getNeeds().get(NeedType.HYGIENE) + "|" +
                    sim.getNeeds().get(NeedType.SOCIAL) + "|" +
                    sim.getNeeds().get(NeedType.FUN) + "|" +
                    sim.getNeeds().get(NeedType.BLADDER) + "|" +
                    jobLevelsStr + "|" +
                    skillLevelsStr + "|" +
                    petsStr + "|" +                  
                    sim.getLastInactiveDays() + "|" +   
                    sim.isCarMaintenancePaid()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    @Override
    public boolean loadGame(Game game) {
        ensureSaveFileExists();

        File file = new File(SAVE_FILE);
        if (file.length() == 0) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String firstLine = reader.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                return false;
            }

            String[] timeParts = firstLine.split("\\|");
            if (timeParts.length < 3) {
                return false;
            }

            GameClock loadedClock = new GameClock(
                Integer.parseInt(timeParts[0]),
                Integer.parseInt(timeParts[1])
            );
            game.setClock(loadedClock);

            int activeSimIndex = Integer.parseInt(timeParts[2]);
            game.clearSimList();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 20) continue;

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
                
                String hotelData = parts[12];
                if (!hotelData.equals("null")) {
                    String[] hotelParts = hotelData.split("-");
                    if (hotelParts.length == 3) {
                        int hotelId = Integer.parseInt(hotelParts[0]);
                        String hotelName = hotelParts[1].replace("_", "-");
                        int hotelValue = Integer.parseInt(hotelParts[2]);
                        sim.setOwnedHotel(new Hotel(hotelId, hotelName, hotelValue));
                    } else {
                        sim.setOwnedHotel(null);
                    }
                } else {
                    sim.setOwnedHotel(null);
                }

                sim.getNeeds().add(NeedType.HUNGER,
                    Integer.parseInt(parts[13]) - sim.getNeeds().get(NeedType.HUNGER));
                sim.getNeeds().add(NeedType.ENERGY,
                    Integer.parseInt(parts[14]) - sim.getNeeds().get(NeedType.ENERGY));
                sim.getNeeds().add(NeedType.HYGIENE,
                    Integer.parseInt(parts[15]) - sim.getNeeds().get(NeedType.HYGIENE));
                sim.getNeeds().add(NeedType.SOCIAL,
                    Integer.parseInt(parts[16]) - sim.getNeeds().get(NeedType.SOCIAL));
                sim.getNeeds().add(NeedType.FUN,
                    Integer.parseInt(parts[17]) - sim.getNeeds().get(NeedType.FUN));
                sim.getNeeds().add(NeedType.BLADDER,
                    Integer.parseInt(parts[18]) - sim.getNeeds().get(NeedType.BLADDER));

                Map<String, Integer> jobLevelsMap = new HashMap<>();
                if (parts.length >= 20) {
                    String jobLevelsRaw = parts[19];
                    if (jobLevelsRaw != null && !jobLevelsRaw.trim().isEmpty()) {
                        String[] jobLevelPairs = jobLevelsRaw.split(",");
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

                if (!jobLevelsMap.containsKey("Jobless")) {
                    jobLevelsMap.put("Jobless", 1);
                }
                sim.setAllJobLevels(jobLevelsMap);

                Map<SkillType, Integer> skillLevelsMap = new EnumMap<>(SkillType.class);
                if (parts.length >= 21) {
                    String skillsRaw = parts[20];
                    if (skillsRaw != null && !skillsRaw.trim().isEmpty()) {
                        String[] skillPairs = skillsRaw.split(",");
                        for (String pair : skillPairs) {
                            String[] keyValue = pair.split(":");
                            if (keyValue.length == 2) {
                                SkillType skillType = SkillType.valueOf(keyValue[0].trim());
                                int value = Integer.parseInt(keyValue[1].trim());
                                skillLevelsMap.put(skillType, value);
                            }
                        }
                    }
                }
                sim.setAllSkillLevels(skillLevelsMap);

                // Deserialize pets
                if (parts.length >= 22) {
                    String petsRaw = parts[21];
                    if (petsRaw != null && !petsRaw.trim().isEmpty()) {
                        String[] petDatas = petsRaw.split(",");
                        //System.out.println("[LoadGame] Loading " + petDatas.length + " pets for Sim: " + sim.getName());
                        for (String petData : petDatas) {
                            Pet pet = Pet.deserialize(petData);
                            if (pet != null) {
                                sim.adoptPet(pet);
                                //System.out.println("[LoadGame] Loaded pet: " + pet.getName() + " for Sim: " + sim.getName());
                            } else {
                                //System.err.println("[LoadGame] Failed to deserialize pet: " + petData);
                            }
                        }
                    } else {
                        //System.out.println("[LoadGame] No pets saved for Sim: " + sim.getName());
                    }
                } else {
                    //System.out.println("[LoadGame] No pets field for Sim: " + sim.getName() + " (parts.length=" + parts.length + ")");
                }
                
                if (parts.length >= 23) {
                	try {
                		int inactiveDays = Integer.parseInt(parts[22]);
                		sim.setLastInactiveDays(inactiveDays);
                	} catch (NumberFormatException e) {
                		sim.setLastInactiveDays(0);
                	}
                } else {
                	sim.setLastInactiveDays(0);
                }

                // Backward compatible: old saves may not include this field.
                if (sim.getOwnedCar() != null && parts.length >= 24) {
                    sim.setCarMaintenancePaid(Boolean.parseBoolean(parts[23]));
                }

                game.addSim(sim);
                
                if (sim.isAlive() && sim.shouldDieFromInactivity()) {
                    sim.setAlive(false);
                }
            }
            game.cleanupDeadSims();

            if (!game.sims().isEmpty()) {
                if (activeSimIndex >= 0 && activeSimIndex < game.sims().size()) {
                    game.setActiveSim(activeSimIndex);
                } else {
                    game.setActiveSim(0);
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Load failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void clearSaveFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            writer.write("");
        } catch (IOException e) {
            System.out.println("Clear save failed: " + e.getMessage());
        }
    }
}
