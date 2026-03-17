# 🎮 THE SIMS – TERMINAL CONSOLE EDITION

A text-based life simulation game where you create and manage virtual characters, balance their needs, build wealth, and survive day-to-day life decisions.

---

## 📌 Overview

**The Sims – Console Edition** is a command-line simulation game that challenges players to manage:

* 🧍 Character needs (hunger, energy, hygiene, etc.)
* 💰 Finances (cash, bank account, loans)
* 🏠 Assets (cars and houses)
* 💼 Careers and income
* ⏱ Time progression and daily cycles

Your goal?
👉 Keep your Sims alive, financially stable, and progressing in life.

---

## 🚀 Quick Start

### New to the game?

👉 Read `TUTORIAL.txt` for a step-by-step walkthrough of gameplay and mechanics.

### Need help?

👉 Check `FAQ.txt` for common questions and strategies.

---

## 🎯 Core Features

---

### 🧠 General Gameplay

* Save progress automatically when quitting
* Start fresh with **New Game**
* Sims spawn at a random street initially
* Built-in tutorial and FAQ system
* 💀 **Death System**:

  * Any need reaching **0 → instant death**
  * If all Sims die → **Game Over**

---

### ⏱ Time System

* Every action advances time by **+1 hour**
* Daily cycle:

  * 🌅 Start: **08:00**
  * 🌙 End: **22:00**
* Needs update every hour

#### 😴 Sleep Mechanics

* 20:00 → Reminder to sleep
* 21:00 → Warning
* 22:00 → Forced faint (skip to next day)

> ⚠️ Sleeping always advances to the next day (08:00)

---

### ❤️ Needs System

Each Sim has dynamic needs:

| Level Range | Status      |
| ----------- | ----------- |
| ≥ 60        | 🟢 Good     |
| 21–59       | 🟡 Moderate |
| ≤ 20        | 🔴 Critical |

* Needs change hourly
* Poor management leads to death

---

### 💰 Money System

Two types of currency:

* **Simcoin** (on-hand cash)
* **Bank Account**

#### Available Actions:

* Deposit / Withdraw
* Apply Loan (max: **$5000**)
* Repay Loan

> Loans are credited directly to Simcoin

---

### 🏠 Asset System

#### Asset Types:

* 🚗 Car — $2000
* 🏡 House — $5000

#### Purchase Options:

| Condition                              | Result        |
| -------------------------------------- | ------------- |
| Enough cash                            | Buy outright  |
| Enough for down payment + loan < $5000 | Buy with loan |
| Loan ≥ $5000                           | ❌ Cannot buy  |
| Cash < down payment                    | ❌ Cannot buy  |

#### Down Payments:

* Car: $400 (+$1600 loan)
* House: $1500 (+$3500 loan)

---

### 💸 Selling Assets

* Car → 60% resale value
* House → 90% resale value
* Must **clear loan first** before selling

---

### 🏦 Loan System

Loan penalties depend on time and assets:

#### After 60 Days:

* Loan > $2000:

  * Own house → house repossessed
  * No house → car repossessed
* Loan < $2000:

  * Car repossessed

#### After 80 Days:

* If **(bank + cash) < loan → 💀 death**

---

### 📍 Location System

#### Available Locations:

* Street (starting point only)
* Park
* Bank
* Restaurant
* Hospital
* Home

#### Travel Methods:

| Method | Effect                    |
| ------ | ------------------------- |
| Walk   | Hunger +10, Energy −15    |
| Drive  | No penalty (requires car) |

---

## 🧭 Game Menu Structure

---

### 🎮 Start Menu

```
1) New Game
2) Continue Game
3) Quit Game
```

---

### 👥 Sims Management Menu

```
1) Create New Sims
2) Select Existing Sims
3) Enter Sim Main Menu
4) Return to Start Menu
5) Quit Game
```

---

### 🧍 Sim Main Menu

```
Time: Day X, XX:00

1) View Status
2) Travel
3) Location Actions
4) Job Management
5) Asset Operations
6) Pass Time (+1 hour)
7) View FAQ
8) Back
9) Quit
```

---

### 📍 Location Menu

```
Current Location: XXX
Car: Yes/No | House: Yes/No

1) Travel to Location
N) Back
```

---

### ⚙️ Location Action Menu

```
Time: Day X, XX:00
Needs: XXX

1) Perform Action
2) Perform Action
N) Back
```

---

### 💼 Job System

Available Jobs:

* Chef
* Doctor
* Engineer
* Influencer
* Jobless

```
Change Job Menu:
1) Select Job
2) Back
```

---

### 🏠 Asset Operations

```
Car: Yes/No | House: Yes/No
Cash: $X | Loan: $X

1) Buy Car
2) Buy House
3) Sell Asset
4) Back
```

---

### 🏦 Bank Menu

```
Deposit: $X | Loan: $X | Cash: $X

1) Work
2) Deposit
3) Withdraw
4) Apply Loan
5) Repay Loan
6) Back
```

---

## ❓ FAQ Highlights

* 💼 Earn money → Get a job + work
* ❤️ Restore needs → Use location actions
* 🚗 Car → Enables free travel (no penalties)
* 🏡 House → Unlocks better actions + bonuses
* 🏦 Bank account → Requires ≥ $500
* 😴 Faint → Happens at 22:00 automatically
* 💀 Death → Any need reaches 0

---

## 🧩 Game Strategy Tips

* ⚖️ Balance needs early — don’t rush money
* 🚗 Buy a car early to reduce travel penalties
* 🏡 House gives long-term efficiency boost
* 💳 Don’t over-loan — repossession is brutal
* ⏳ Time management is EVERYTHING

---