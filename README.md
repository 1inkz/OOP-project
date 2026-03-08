**Features**

General
1. Save game progress on Quit Game option
2. Reset all on New Game option
4. Sims will start on a random street first
5. Tutorial: tutorial for new game
6. FAQ: option in main menu for gameplay review
7. Die: Any needs = 0, Sims die(remove), all Sims die, game over

Time System
1. Hour-based: All actions + 1 hour 
2. Daily: 24:00 jump to next day, interest 0.05%/day
3. Needs: Needs increase/decrease every hour
4. Sleep: 2000 msg to remind sleep, 2100 warning msg to sleep, 2200 force faint

Needs System
1. Status: ≥60 Green, 21-59 Yellow, ≤20 Red
2. IMPORTANT: Sleep will move to next day.

Money
1. Type: Simcoin(on hand), Bank acc
2. Deposit/Withdraw: betweem bank acc and simcoin
3. Loan: Max $3000, loan direct to simcoin
4. Pay loan: deduct from simcoin

Asset
1. Type: Car/House
2. Car $2000, $400+$1600(loan) 
3. House $5000, $1500+$3500(loan)
4. Sell: purchase value*depreciation rate (Car60%, House90%) (Clear loan first)

Location System
1. Type: Street(only initial)/Park/Bank/Restaurant/Hospital/Home
2. Travel method: Walk(Hunger+10, Energy-15), Drive

**Hierarchy**
SIMS GAME - Start Menu
1) New Game 
	 SIMS GAME - Sims Management Menu 
	 2) Create New Sim 
		Input name → Select type (Child/Adult/Elder) → Create success
		Trigger 3-screen Tutorial (Press Enter to proceed)
		Enter Sim Main Menu
		3) Back 
	2) Continue Game 
		Enter Sim Main Menu
	3) Exit 

============ SIMS GAME - [Name] Main Menu ============
Time: Day X, XX:00
1) View [Name] status
2) Travel to Location
3) View [Location] Action Menu
4) Find Job/ Change Job
5) Asset Operations [Buy/Sell Car/House]
6) Bank Operations [Deposit/Withdraw/Loan/Repay/Check Balance]
7) Pass Time (1 Hour)
8) View FAQ
9) Back [To Sims Management Menu]
10) Quit Game 

============ SIMS GAME - Location Selection Menu ============
Current Location: XXX | Own Car: Yes/No | Own House: Yes/No
1) [Location 1]
2) [Location 2]
N) Back [To Main Menu]

============ SIMS GAME - [Location Name] Action Menu ============
Current Time: Day X, XX:00 | Needs: XXX 
1) [Location-Specific Action 1]
2) [Location-Specific Action 2]
N) Back [To Location Selection Menu]

============ SIMS GAME - Change Sim's Job ============
Current Job: XXX (Level X)
1) Chef
2) Doctor
3) Engineer
4) Influencer
5) Jobless
6) Back [To Main Menu]

============ SIMS GAME - Asset Operations Menu ============
Current Asset: Own Car: Yes/No | Own House: Yes/No | Cash: $X | Loan: $X
1) Buy Basic Car [Down payment $400 / Full cash $2000]
2) Buy Basic House [Down payment $1500 / Full cash $5000]
3) Sell Asset [Available if own Car/House]
4) Back [To Main Menu]

============ SIMS GAME - Bank Operations Menu ============
 Deposit: $X | Loan: $X | Simcoin: $X
1) Open Bank Account [Cash ≥ $500]
2) Deposit Cash
3) Withdraw Cash
4) Apply for Loan [Max $10000]
5) Repay Loan
6) Check Account Balance
7) Back [To Main Menu]

============ SIMS GAME - FAQ ============
1. How to earn money? → Find job, work in corresponding location
2. How to restore needs? → Use location-specific actions (e.g., CleanPublic for Hygiene)
3. How to buy Car/House? → Asset Operations in Bank (down payment + loan available)
4. What's Car's use? → Drive with no need cost; walk only without Car
5. What's House's use? → Unlock Home, high-efficiency hygiene actions + decay bonus
6. How to open bank account? → Cash ≥ $500, Bank Operations in Bank
7. Faint rule? → Instant faint at 22:00, jump to next day 8:00
8. When is Sim eliminated? → Any NeedType drops to 0