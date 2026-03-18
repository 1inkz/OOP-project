# JAVADOC ADDITION PLAN - Comprehensive Analysis

**Project:** OOP Sims CLI  
**Total Java Files:** 123  
**Analysis Date:** March 18, 2026  
**Status:** 89 files have NO Javadoc; 34 files have PARTIAL Javadoc

---

## EXECUTIVE SUMMARY

### Current State
- **Files with NO Javadoc:** 89 (72%)
- **Files with PARTIAL Javadoc:** 34 (28%)
- **Total Methods/Fields Needing Documentation:** 500+ elements

### Recommended Approach
Implement Javadoc in **3 phases**:
1. **Phase 1:** Core infrastructure classes (Game, Sim, Pet systems)
2. **Phase 2:** Game managers and components (Time, Sim, Location managers)
3. **Phase 3:** UI, Actions, Jobs, Locations, and utilities

---

## DETAILED FILE-BY-FILE BREAKDOWN

### FILES WITH NO JAVADOC (89 files)

#### ROOT LEVEL (3 files)
```
src/simscli/ExitGuard.java
├─ Class: ExitGuard
├─ Methods: install(Game game), markNormalExit()
├─ Fields: normalExit (AtomicBoolean), hookInstalled (AtomicBoolean)
└─ Needs: Class doc + all 2 methods + 2 fields

src/simscli/Main.java
├─ Class: Main
├─ Methods: main(String[] args)
├─ Fields: (none)
└─ Needs: Class doc + main method

src/simscli/GameClock.java
├─ Class: GameClock
├─ Methods: advanceByRealTime(), onHourPassed(), getHoursPassedFromAccumulator(), 
│           spendMinutes(), getDayNumber(), getMinuteOfDay(), getHour(), getMinute(),
│           getMinutesLeftToday(), getFormattedTime(), clamp(), resetToNextDayMorning(),
│           resetNewGame()
├─ Fields: MINUTES_PER_DAY, GAME_MINUTES_PER_REAL_SECOND, dayNumber, minuteOfDay,
│          minuteRemainder, accumulatedMinutes
└─ Needs: Class doc + 13 methods + 6 fields

src/simscli/SaveGame.java
├─ Class: SaveGame
├─ Methods: ensureSaveFileExists(), hasValidSaveData(), saveGame(Game game),
│           loadGame(Game game), clearSaveFile()
├─ Fields: SAVE_FILE (String)
└─ Needs: Class doc + 5 methods + 1 field
```

#### ACTIONS: SIMPLE (13 files - mostly simple)
```
src/simscli/actions/simple/BrushTeeth.java → Method: perform()
src/simscli/actions/simple/Cleaning.java → Method: perform()
src/simscli/actions/simple/EatMeal.java → Method: perform()
src/simscli/actions/simple/EatSnack.java → Method: perform()
src/simscli/actions/simple/Exercise.java → Method: perform()
src/simscli/actions/simple/Nap.java → Method: perform()
src/simscli/actions/simple/PlayGame.java → Method: perform()
src/simscli/actions/simple/ReadBook.java → Method: perform()
src/simscli/actions/simple/Shower.java → Method: perform()
src/simscli/actions/simple/Sleep.java → Method: perform()
src/simscli/actions/simple/Socialise.java → Method: perform()
src/simscli/actions/simple/UseToilet.java → Method: perform()
src/simscli/actions/simple/WatchTV.java → Method: perform()
src/simscli/actions/simple/Work.java → Methods: name(), perform(), grantWorkSkillProgress()
                                       Fields: RED, GREEN, RESET, ENERGY_COST, 
                                              HYGIENE_COST, FUN_COST
```

#### ACTIONS: PET (6 files)
```
src/simscli/actions/pet/BuyPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
└─ Needs: Class + 2 methods

src/simscli/actions/pet/FeedPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: pet (Pet)
└─ Needs: Class + 2 methods + pet field

src/simscli/actions/pet/GroomPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: pet (Pet)
└─ Needs: Class + 2 methods + pet field

src/simscli/actions/pet/PlayWithPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: pet (Pet)
└─ Needs: Class + 2 methods + pet field

src/simscli/actions/pet/ShowerPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: pet (Pet)
└─ Needs: Class + 2 methods + pet field

src/simscli/actions/pet/SleepWithPet.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: pet (Pet)
└─ Needs: Class + 2 methods + pet field
```

#### ACTIONS: OTHER (3 files)
```
src/simscli/actions/ActionFactory.java
├─ Methods: create(ActionType type), createFeedPet(Pet pet), createShowerPet(Pet pet),
│           createPlayWithPet(Pet pet), createSleepWithPet(Pet pet), createGroomPet(Pet pet)
└─ Needs: Class doc + 6 methods

src/simscli/actions/ActionType.java
├─ (Enum - no methods to document)
└─ Needs: Class doc only

src/simscli/actions/GetCheckup.java
├─ Methods: name(), perform(Sim sim, GameContext ctx)
├─ Fields: CHECKUP_COST (int)
└─ Needs: Class doc + 2 methods + 1 field
```

#### ASSETS (6 files)
```
src/simscli/asset/Asset.java
├─ Methods: isCar(), isHouse(), isHotel(), getValue(), getId(), getName(), getAssetType()
├─ Fields: id, name, purchaseValue
└─ Needs: Class doc + 7 methods + 3 fields

src/simscli/asset/Car.java
├─ Methods: getTravelMultiplier(), getAssetType(), sellValue()
├─ Fields: travelMultiplier (double)
└─ Needs: Class doc + 3 methods + 1 field

src/simscli/asset/Hotel.java
├─ Methods: getAssetType(), sellValue()
└─ Needs: Class doc + 2 methods

src/simscli/asset/House.java
├─ Methods: getAssetType(), sellValue()
└─ Needs: Class doc + 2 methods

src/simscli/asset/Ownable.java
├─ (Interface - no methods)
└─ Needs: Class doc only

src/simscli/asset/Sellable.java
├─ (Interface - no methods)
└─ Needs: Class doc only
```

#### BANK (6 files)
```
src/simscli/bank/BankAccount.java
├─ Methods: deposit(Money amount), withdraw(Money amount), getBalance()
├─ Fields: balance (Money)
└─ Needs: Class doc + 3 methods + 1 field

src/simscli/bank/BankService.java
├─ Methods: deposit(int amount), withdraw(int amount), applyLoan(int amount),
│           repayLoan(int amount), settleInterest(), getBalance(), getLoan()
├─ Fields: account (BankAccount), loan (LoanAccount), interestPolicy (InterestPolicy)
└─ Needs: Class doc + 7 methods + 3 fields

src/simscli/bank/InterestPolicy.java
├─ (Interface - no methods)
└─ Needs: Class doc only

src/simscli/bank/LoanAccount.java
├─ Methods: applyLoan(Money amount), repayLoan(Money amount), getLoanBalance()
├─ Fields: loanBalance (Money), LOAN_LIMIT (int)
└─ Needs: Class doc + 3 methods + 2 fields

src/simscli/bank/Money.java
├─ Methods: getAmount(), add(Money other), subtract(Money other)
├─ Fields: amount (int)
└─ Needs: Class doc + 3 methods + 1 field

src/simscli/bank/SimpleInterestPolicy.java
├─ Methods: calculateInterest(Money balance)
├─ Fields: RATE (double)
└─ Needs: Class doc + 1 method + 1 field
```

#### GAME (Multiple classes)
```
src/simscli/game/GameClock.java [DUPLICATE - see root level]

src/simscli/game/GameContext.java
├─ Methods: game(), getClock(), resetToNextDayMorning(), checkTimeRules(),
│           intRange(String prompt, int min, int max), line(String prompt)
├─ Fields: game (Game)
└─ Needs: Class doc + 6 methods + 1 field
```

#### JOBS (7 files)
```
src/simscli/jobs/BankTellerJob.java
├─ Methods: primarySkills()
└─ Needs: Class doc + 1 method

src/simscli/jobs/ChefJob.java
├─ Methods: primarySkills()
└─ Needs: Class doc + 1 method

src/simscli/jobs/DoctorJob.java
├─ Methods: primarySkills()
└─ Needs: Class doc + 1 method

src/simscli/jobs/InfluencerJob.java
├─ Methods: getWorkLocations(), primarySkills()
└─ Needs: Class doc + 2 methods

src/simscli/jobs/Job.java
├─ (Abstract class - no concrete methods)
└─ Needs: Class doc only

src/simscli/jobs/JobFactory.java
├─ Methods: create(String name)
└─ Needs: Class doc + 1 method

src/simscli/jobs/JoblessJob.java
├─ Methods: primarySkills()
└─ Needs: Class doc + 1 method
```

#### LOCATIONS (9 files)
```
src/simscli/location/Bank.java
├─ Methods: actions(Sim sim), canWorkHere(Sim sim), onEnter(Sim sim)
└─ Needs: Class doc + 3 methods

src/simscli/location/Home.java
├─ Methods: canEnter(Sim sim), actions(Sim sim)
└─ Needs: Class doc + 2 methods

src/simscli/location/Hospital.java
├─ Methods: key(), name(), actions(Sim sim), canWorkHere(Sim sim), onEnter(Sim sim)
└─ Needs: Class doc + 5 methods

src/simscli/location/Location.java
├─ Methods: key(), name(), actions(Sim sim), canEnter(Sim sim), onEnter(Sim sim)
└─ Needs: Class doc + 5 methods

src/simscli/location/Park.java
├─ Methods: actions(Sim sim), canWorkHere(Sim sim)
└─ Needs: Class doc + 2 methods

src/simscli/location/PetStore.java
├─ Methods: key(), name(), actions(Sim sim)
└─ Needs: Class doc + 3 methods

src/simscli/location/Restaurant.java
├─ Methods: actions(Sim sim), canWorkHere(Sim sim), perform(Sim sim, GameContext ctx)
└─ Needs: Class doc + 3 methods

src/simscli/location/Street.java
├─ Methods: key(), name(), actions(Sim sim), onEnter(Sim sim)
└─ Needs: Class doc + 4 methods
```

#### PETS (7 files)
```
src/simscli/pets/Bunny.java
├─ (Enum constant - no methods to document)
└─ Needs: Class doc only

src/simscli/pets/Cat.java
├─ (Enum constant - no methods to document)
└─ Needs: Class doc only

src/simscli/pets/Dog.java
├─ (Enum constant - no methods to document)
└─ Needs: Class doc only

src/simscli/pets/PetFactory.java
├─ Methods: create(PetType type, String name)
└─ Needs: Class doc + 1 method

src/simscli/pets/PetType.java
├─ Methods: getDisplayName(), getPrice()
├─ Fields: displayName (String), price (int)
└─ Needs: Class doc + 2 methods + 2 fields
```

#### SIMS RELATED (6 files)
```
src/simscli/sims/AdultSim.java
├─ Methods: hourlyDecay()
└─ Needs: Class doc + 1 method

src/simscli/sims/ChildSim.java
├─ Methods: hourlyDecay()
└─ Needs: Class doc + 1 method

src/simscli/sims/ElderSim.java
├─ Methods: hourlyDecay()
└─ Needs: Class doc + 1 method

src/simscli/sims/SimType.java
├─ (Enum - no methods)
└─ Needs: Class doc only
```

#### STATS (3 files)
```
src/simscli/stats/NeedType.java
├─ (Enum - no methods)
└─ Needs: Class doc only

src/simscli/stats/SkillType.java
├─ Methods: displayName()
├─ Fields: displayName (String)
└─ Needs: Class doc + 1 method + 1 field

src/simscli/stats/Skills.java
├─ Methods: get(SkillType type), set(SkillType type, int value),
│           gain(SkillType type, int amount), isMaxed(SkillType type),
│           average(SkillType... types), snapshot(), loadFrom(),
│           validateType(SkillType type), clamp(int value)
├─ Fields: MIN_SKILL, MAX_SKILL, skills (EnumMap)
└─ Needs: Class doc + 9 methods + 3 fields
```

#### UI CLASSES (6 files)
```
src/simscli/ui/BusinessUIManager.java
├─ Methods: showTravelToLocationMenu(), showDoLocationActionsMenu(Location location),
│           showChangeJobMenu(), showAssetOperationsMenu(), showSellAssetMenu()
├─ Fields: game, in, uiHelper
└─ Needs: Class doc + 5 methods + 3 fields

src/simscli/ui/ConsoleUI.java
├─ Methods: run()
├─ Fields: game, in, uiHelper, simManager, simUIManager, menuUIManager,
│          businessUIManager, isGameReset
└─ Needs: Class doc + 1 method + 8 fields

src/simscli/ui/Input.java
├─ Methods: line(String prompt), intRange(String prompt, int min, int max)
├─ Fields: sc (Scanner)
└─ Needs: Class doc + 2 methods + 1 field

src/simscli/ui/MenuUIManager.java
├─ Methods: showInitialMenu(), showSimManagementMenu()
├─ Fields: game, in, uiHelper, simUIManager, isGameLoaded, isGameReset
└─ Needs: Class doc + 2 methods + 6 fields

src/simscli/ui/SimUIManager.java
├─ Methods: showTutorial(), createNewSim(), selectExistingSim(), printSimStatus(Sim sim)
├─ Fields: game, in, uiHelper, menuUIManager, tutorialShown
└─ Needs: Class doc + 4 methods + 5 fields

src/simscli/ui/UIHelper.java
├─ Methods: getNeedColor(int value), printDynamicTitle(String title, String color),
│           confirmStartNewGame()
├─ Fields: RED, GREEN, YELLOW, BLUE, PURPLE, DARK_RED, CYAN, RESET, in
└─ Needs: Class doc + 3 methods + 9 fields
```

#### WORLD OBJECTS (9 files - no methods, just markers/interfaces)
```
src/simscli/world/Bed.java → (Marker class)
src/simscli/world/Bookshelf.java → (Marker class)
src/simscli/world/Computer.java → (Marker class)
src/simscli/world/Fridge.java → (Marker class)
src/simscli/world/ShowerStall.java → (Marker class)
src/simscli/world/TV.java → (Marker class)
src/simscli/world/Toilet.java → (Marker class)
src/simscli/world/Treadmill.java → (Marker class)
src/simscli/world/Usable.java → Interface with 3 method definitions
                               Methods: use(), isAvailable(), interact()
                               Needs: Class doc + 3 method signatures
```

#### TEST FILES (6 files - optional but recommended)
```
test/simscli/ActionEffectTest.java → 2 test methods
test/simscli/BoundedStatTest.java → 3 test methods
test/simscli/JobFactoryTest.java → 3 test methods
test/simscli/JoblessBehaviorTest.java → 3 test methods
test/simscli/LocationTravelTest.java → 2 test methods
test/simscli/RestaurantRejectionTest.java → 2 test methods
Total: 15 test methods
```

---

## FILES WITH PARTIAL JAVADOC (34 files)

These files have SOME Javadoc (class-level or some methods) but need completion:

```
src/simscli/actions/Action.java
└─ Has: Class doc (1 block)
└─ Missing: name() and perform() method docs

src/simscli/actions/ActionUIAdapter.java
└─ Has: Class doc + 2 method docs
└─ Missing: 1+ method docs

src/simscli/actions/banking/ApplyLoan.java
├─ Has: Class doc
└─ Missing: perform() method, GREEN & RESET field docs

src/simscli/actions/banking/Deposit.java
├─ Has: Class doc
└─ Missing: perform() method, 3 color fields

src/simscli/actions/banking/RepayLoan.java
├─ Has: Class doc
└─ Missing: perform() method, 3 color fields

src/simscli/actions/banking/Withdraw.java
├─ Has: Class doc
└─ Missing: perform() method, 3 color fields

src/simscli/actions/interactive/FeedPetMenu.java
├─ Has: Class doc
└─ Missing: 2 methods (name, perform)

src/simscli/actions/interactive/GroomPetMenu.java
├─ Has: Class doc
└─ Missing: 2 methods (name, perform)

src/simscli/actions/interactive/PlayWithPetMenu.java
├─ Has: Class doc
└─ Missing: 2 methods (name, perform)

src/simscli/actions/interactive/ShowerPetMenu.java
├─ Has: Class doc
└─ Missing: 2 methods (name, perform)

src/simscli/actions/interactive/SleepWithPetMenu.java
├─ Has: Class doc
└─ Missing: 2 methods (name, perform)

src/simscli/bank/BankingSystem.java
├─ Has: Class doc (incomplete)
├─ Methods: 8 (ALL missing docs)
├─ Fields: 2 (missing docs)
└─ Critical: Main banking interface

src/simscli/game/ActionExecutor.java
├─ Has: Class + 2 method docs
├─ Missing: 2 formatting methods

src/simscli/game/ConsoleGameLogger.java
├─ Has: Class doc
├─ 3 Methods: info(), warn(), error() [ALL MISSING DOCS]

src/simscli/game/Game.java
├─ Has: Class doc ONLY
├─ Missing: 26 methods (CRITICAL - main game class)
├─ Missing: 8 fields (CRITICAL)
└─ PRIORITY: Must be 100% documented

src/simscli/game/LoanManager.java
├─ Has: Class doc + 1 method doc
├─ Missing: 1 field doc
└─ Status: Almost complete

src/simscli/game/LocationManager.java
├─ Has: Class doc + 3 method docs
├─ Missing: 8 methods, 3 fields

src/simscli/game/SimManager.java
├─ Has: Class doc + 1 method doc
├─ Missing: 8 methods, 3 fields

src/simscli/game/TimeManager.java
├─ Has: Class doc + 6 method docs
├─ Missing: 3 methods, 3 fields

src/simscli/location/LocationActionFactory.java
├─ Has: Class + 2 method docs
└─ Status: Mostly complete

src/simscli/pets/ExperienceUtils.java
├─ Has: 1 method doc
└─ Status: Incomplete

src/simscli/pets/Pet.java
├─ Has: 2 method docs
├─ Missing: 19 methods, 10 fields (CRITICAL)
└─ PRIORITY: Core pet class - needs complete documentation

src/simscli/sims/ConsoleGameLogger.java
├─ Has: Class doc
├─ 3 Methods: info(), warn(), error() [ALL MISSING DOCS]

src/simscli/sims/Needs.java
├─ Has: Class doc + 1 field doc
├─ Missing: 6 methods, 1 field (from stats package also exists)

src/simscli/sims/Sim.java
├─ Has: Class doc + 1 field doc
├─ Missing: 59 methods, 11 fields (CRITICAL - Core Sim class)
└─ PRIORITY: Must be 100% documented - this is the main entity

src/simscli/sims/SimAssetsComponent.java
├─ Has: Class doc + 3 method docs
├─ Missing: 16 methods, 8 fields

src/simscli/sims/SimBankingComponent.java
├─ Has: Class doc ONLY
├─ Missing: 13 methods, 2 fields (ALL methods need docs)

src/simscli/sims/SimEmploymentComponent.java
├─ Has: Class doc + 1 method doc
├─ Missing: 11 methods, 6 fields

src/simscli/sims/SimPetsComponent.java
├─ Has: Class doc + 1 method doc
├─ Missing: 2 methods, 3 fields

src/simscli/sims/SimStatsComponent.java
├─ Has: Class doc ONLY
├─ Missing: 11 methods, 3 fields (ALL methods need docs)

src/simscli/stats/BoundedStat.java
├─ Has: Class doc only
├─ Missing: 6 methods, 1 field

src/simscli/stats/Effect.java
├─ Has: Class doc + 2 method docs
├─ Missing: 2 methods, 1 field

src/simscli/stats/Needs.java
├─ Has: Class doc + 1 field doc
├─ Missing: 6 methods, 1 field (Note: Duplicate in sims package)
```

---

## IMPLEMENTATION PHASES

### PHASE 1: CRITICAL CORE CLASSES (Priority 1)
**Estimated effort:** 8-12 hours

**MUST document these classes completely:**
1. `Game.java` - 26 methods + 8 fields
2. `Sim.java` - 59 methods + 11 fields  
3. `Pet.java` - 19 methods + 10 fields
4. `BankingSystem.java` - 8 methods + 2 fields

**Why:** These are the fundamental entities of your system. Every other class depends on their clear documentation.

---

### PHASE 2: GAME INFRASTRUCTURE & MANAGERS (Priority 2)
**Estimated effort:** 6-8 hours

**Complete documentation for:**
1. `GameClock.java` - 13 methods + 6 fields
2. `TimeManager.java` - 9 methods + 3 fields
3. `SimManager.java` - 9 methods + 3 fields
4. `LocationManager.java` - 11 methods + 3 fields
5. `ActionExecutor.java` - 4 methods
6. Game component classes (SimAssetsComponent, SimBankingComponent, etc.)

---

### PHASE 3: UI, ACTIONS, AND UTILITIES (Priority 3)
**Estimated effort:** 8-10 hours

Complete remaining classes:
- UI managers and Input classes
- Action classes (simple, pet, banking, interactive)
- Job classes and factories
- Location classes
- Stats and bank classes
- World objects

---

## QUICK STATISTICS BY CATEGORY

| Category | Total | No Javadoc | Partial | Methods/Fields Missing |
|----------|-------|-----------|---------|------------------------|
| Actions | 25 | 9 | 16 | ~45 |
| Assets | 6 | 6 | 0 | ~20 |
| Bank | 6 | 6 | 0 | ~20 |
| Game Core | 10 | 3 | 7 | 130+ |
| Jobs | 7 | 7 | 0 | ~8 |
| Locations | 9 | 9 | 0 | ~23 |
| Pets | 7 | 5 | 2 | ~26 |
| Sims | 11 | 3 | 8 | 180+ |
| Stats | 6 | 3 | 3 | ~25 |
| UI | 6 | 6 | 0 | ~20 |
| World | 9 | 9 | 0 | 0 |
| Test | 6 | 0 | 0 | ~15 |
| **TOTAL** | **123** | **89** | **34** | **500+** |

---

## RECOMMENDED JAVADOC TEMPLATE

For standardization, use this template for all new documentation:

### For Classes:
```java
/**
 * [One-line summary of class purpose].
 * 
 * [Detailed explanation of what this class does, its responsibilities,
 * and how it fits into the system. Include any important constraints
 * or design patterns used.]
 * 
 * @see [Related classes if applicable]
 */
public class ClassName {
```

### For Methods:
```java
/**
 * [One-line summary of what this method does].
 * 
 * [Detailed explanation if the method behavior is complex or has side effects.]
 * 
 * @param paramName [Description of what this parameter represents or expects]
 * @return [Description of what is returned and under what conditions]
 * @throws [ExceptionType] [When this exception is thrown]
 */
public ReturnType methodName(Type param) {
```

### For Fields:
```java
/** [Brief description of what this field stores and its purpose]. */
private Type fieldName;
```

---

## ESTIMATED TIMELINE

- **Phase 1 (Core Classes):** 8-12 hours → 4 files fully documented
- **Phase 2 (Managers & Components):** 6-8 hours → 10 files  
- **Phase 3 (Remaining):** 8-10 hours → 70+ files

**Total Estimated Effort:** 22-30 hours (or ~3-4 development days)

---

## NEXT STEPS

1. Start with Phase 1 - document Game.java, Sim.java, Pet.java, BankingSystem.java
2. Use the provided Javadoc template for consistency
3. Run `javadoc` command to generate and validate documentation
4. Once Phase 1 is complete, proceed to Phase 2
5. Test documentation viewer (e.g., IDE tooltips, generated HTML)

---

**Report Generated:** March 18, 2026  
**Total Files Analyzed:** 123  
**Completion Status:** 0% (Ready for implementation)
