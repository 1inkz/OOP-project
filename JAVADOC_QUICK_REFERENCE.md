# JAVADOC IMPLEMENTATION - QUICK REFERENCE GUIDE

## AT A GLANCE

- **Total Files:** 123
- **No Javadoc:** 89 files (72%)
- **Partial Javadoc:** 34 files (28%)
- **Estimated Work:** 22-30 hours

---

## PRIORITY 1: MUST DOCUMENT FIRST (4 critical files)

| File | Methods | Fields | Why Priority |
|------|---------|--------|--------------|
| `src/simscli/game/Game.java` | 26 ❌ | 8 ❌ | Primary game controller - everything depends on this |
| `src/simscli/sims/Sim.java` | 59 ❌ | 11 ❌ | Core entity class - the main actor in your system |
| `src/simscli/pets/Pet.java` | 19 ❌ | 10 ❌ | Pet system - heavily used across codebase |
| `src/simscli/bank/BankingSystem.java` | 8 ❌ | 2 ❌ | Financial system - critical for game logic |

**Effort:** 8-12 hours | **Impact:** Highest

---

## PRIORITY 2: GAME INFRASTRUCTURE (7 files)

| File | Status | Work Needed |
|------|--------|------------|
| `src/simscli/game/GameClock.java` | ❌ No docs | Class + 13 methods + 6 fields |
| `src/simscli/game/TimeManager.java` | ⚠️ Partial | 3 methods + 3 fields remaining |
| `src/simscli/game/SimManager.java` | ⚠️ Partial | 8 methods + 3 fields remaining |
| `src/simscli/game/LocationManager.java` | ⚠️ Partial | 8 methods + 3 fields remaining |
| `src/simscli/game/ActionExecutor.java` | ⚠️ Partial | 2 methods |
| `src/simscli/sims/SimBankingComponent.java` | ⚠️ Partial | 13 methods + 2 fields |
| `src/simscli/sims/SimStatsComponent.java` | ⚠️ Partial | 11 methods + 3 fields |

**Effort:** 6-8 hours | **Impact:** High

---

## PRIORITY 3: COMPONENT CLASSES (8 files)

These are Sim sub-components and utilities:

```
SimAssetsComponent.java        - 16 methods + 8 fields
SimEmploymentComponent.java    - 11 methods + 6 fields
SimPetsComponent.java          - 2 methods + 3 fields
BoundedStat.java             - 6 methods + 1 field
Effect.java                  - 4 methods + 1 field
Skills.java                  - 9 methods + 3 fields
Needs.java (in sims/)        - 6 methods + 1 field
ActionExecutor.java (partial) - 2 methods
```

**Effort:** 5-6 hours | **Impact:** Medium-High

---

## PRIORITY 4: UI LAYER (6 files)

| File | Status | Methods | Fields |
|------|--------|---------|--------|
| `ConsoleUI.java` | ❌ | 1 | 8 |
| `BusinessUIManager.java` | ❌ | 5 | 3 |
| `MenuUIManager.java` | ❌ | 2 | 6 |
| `SimUIManager.java` | ❌ | 4 | 5 |
| `UIHelper.java` | ❌ | 3 | 9 |
| `Input.java` | ❌ | 2 | 1 |

**Effort:** 3-4 hours | **Impact:** Medium

---

## PRIORITY 5: ACTION CLASSES (25 files)

### Simple Actions (14 files)
```
BrushTeeth, Cleaning, EatMeal, EatSnack, Exercise, Nap, PlayGame,
ReadBook, Shower, Sleep, Socialise, UseToilet, WatchTV, Work.java
```
Most have 1-4 methods, mostly `name()` and `perform()` methods.

### Pet Actions (6 files)
```
BuyPet, FeedPet, GroomPet, PlayWithPet, ShowerPet, SleepWithPet.java
```
Each has 2-3 methods + 1 field (pet).

### Banking Actions (4 files)
```
ApplyLoan, Deposit, RepayLoan, Withdraw.java
```
Each has class doc but methods + fields need docs.

### Interactive Menus (5 files)
```
FeedPetMenu, GroomPetMenu, PlayWithPetMenu, ShowerPetMenu, SleepWithPetMenu.java
```
Each has 2 methods needing docs.

**Effort:** 4-5 hours | **Impact:** Low-Medium

---

## PRIORITY 6: STATIC CLASSES & UTILITIES (25 files)

| Category | Count | Work Per File | Total Work |
|----------|-------|--------------|-----------|
| Locations | 9 | Class + 2-5 methods | ~20 methods |
| Assets | 6 | Class + 2-7 methods | ~20 methods |
| Jobs | 7 | Class + 0-2 methods | ~8 methods |
| Pets (enums) | 3 | Class only | ~0 methods |
| Stats Enums | 2 | Class only | ~0 methods |
| World Objects | 9 | Class only | ~0 methods |
| Root Classes | 3 | Class + 1-13 methods | ~20 methods |

**Effort:** 4-5 hours | **Impact:** Low

---

## PRIORITY 7: TEST FILES (6 files) - OPTIONAL

```
ActionEffectTest.java           - 2 methods
BoundedStatTest.java           - 3 methods
JobFactoryTest.java            - 3 methods
JoblessBehaviorTest.java       - 3 methods
LocationTravelTest.java        - 2 methods
RestaurantRejectionTest.java   - 2 methods
```

**Effort:** 1-2 hours | **Impact:** Low | **Optional:** Yes

---

## QUICK CHECKLIST BY PHASE

### Phase 1 (8-12 hours)
- [ ] Game.java - Complete 26 methods + 8 fields
- [ ] Sim.java - Complete 59 methods + 11 fields
- [ ] Pet.java - Complete 19 methods + 10 fields
- [ ] BankingSystem.java - Complete 8 methods + 2 fields

### Phase 2 (6-8 hours)
- [ ] GameClock.java - Complete all
- [ ] TimeManager.java - Complete remaining
- [ ] SimManager.java - Complete remaining
- [ ] LocationManager.java - Complete remaining
- [ ] ActionExecutor.java - Complete remaining
- [ ] Sim Component classes:
  - [ ] SimBankingComponent.java
  - [ ] SimStatsComponent.java
  - [ ] SimAssetsComponent.java
  - [ ] SimEmploymentComponent.java
  - [ ] SimPetsComponent.java

### Phase 3 (8-10 hours)
- [ ] UI classes (6 files)
- [ ] Action classes (25 files)
- [ ] Location classes (9 files)
- [ ] Asset classes (6 files)
- [ ] Job classes (7 files)
- [ ] Utility/Stats classes (15 files)

### Optional
- [ ] Test files (6 files)

---

## TOP 10 FILES BY EFFORT

| Rank | File | Effort | Methods | Fields |
|------|------|--------|---------|--------|
| 1 | Sim.java | ⭐⭐⭐⭐⭐ | 59 | 11 |
| 2 | Game.java | ⭐⭐⭐⭐⭐ | 26 | 8 |
| 3 | Pet.java | ⭐⭐⭐⭐ | 19 | 10 |
| 4 | SimBankingComponent.java | ⭐⭐⭐⭐ | 13 | 2 |
| 5 | SimAssetsComponent.java | ⭐⭐⭐⭐ | 16 | 8 |
| 6 | SimEmploymentComponent.java | ⭐⭐⭐ | 11 | 6 |
| 7 | SimStatsComponent.java | ⭐⭐⭐ | 11 | 3 |
| 8 | LocationManager.java | ⭐⭐⭐ | 11 | 3 |
| 9 | GameClock.java | ⭐⭐⭐ | 13 | 6 |
| 10 | Skills.java | ⭐⭐⭐ | 9 | 3 |

---

## FILES WITH MINIMAL WORK (Easy wins)

These files need minimal documentation (mostly interface/enum doc only):

- `ActionType.java` - Enum only
- `SimType.java` - Enum only
- `NeedType.java` - Enum only
- `SkillType.java` - Has 1 method
- `Ownable.java` - Interface only
- `Sellable.java` - Interface only
- `InterestPolicy.java` - Interface only
- `Job.java` - Abstract class only
- `Usable.java` - Interface (3 methods minimal)
- `BankTellerJob.java` - 1 method
- `ChefJob.java` - 1 method
- `DoctorJob.java` - 1 method
- `JoblessJob.java` - 1 method

**Quick Win:** Document all these 13 files in 1-2 hours

---

## VALIDATION STEPS

After completing Javadoc additions:

1. **Generate Javadoc HTML:**
   ```bash
   javadoc -d docs src/simscli/**/*.java
   ```

2. **Check IDE tooltips** - Hover over methods/classes in your IDE

3. **Validate syntax:**
   ```bash
   javadoc -private -quiet src/simscli/**/*.java
   ```

4. **Review completeness** - No `@author`, `@version` tags needed unless project requires

---

## NOTES

- **Key principle:** Document what, why, and how - not just what the code does
- **Template provided** in main plan document
- **Estimated total time:** 22-30 development hours
- **Start with Priority 1** - these are least stable and most important

---

**Last Updated:** March 18, 2026  
**Total Coverage Analysis:** Complete (123 files analyzed)
