# 🎮 THE SIMS - TERMINAL CONSOLE EDITION

A Java-based life simulation game you play directly in the terminal.  
Manage your Sims, survive daily life, earn money, and build your dream lifestyle!

---

## 🧠 Overview

Create and manage multiple Sims, but control one at a time.

### 🎯 Your Goal
- ❤️ Stay alive
- 💰 Stay financially stable
- 🏡 Build your lifestyle (house, car, hotel, pets)

---

## ⚙️ Core Systems

### 👤 Sims
Each Sim has:
- Needs (must stay above 0!)
- Job and career progression
- 💵 Cash, 🏦 bank deposits, and 💳 loans
- 🚗 Assets (car, house, hotel)
- 🐶 Pets
- 📍 Current location

---

### ❤️ Needs System

Track and manage:
- Hunger 🍔
- Energy ⚡
- Hygiene 🚿
- Social 💬
- Fun 🎮
- Bladder 🚽

#### 📊 Need Levels
- 🟢 60+ → Good
- 🟡 21–59 → Warning
- 🔴 0–20 → Critical

⚠️ Important:
- Hunger / Energy at 0 = 💀 death
- Other needs may trigger penalties (hospital, burnout, accidents)

---

### ⏰ Time System
- Most actions take **1 in-game hour**
- Travel also consumes time and affects needs
- 🌙 Late nights drain energy faster (midnight warning!)

---

### 💼 Jobs & Income

Available jobs:
- 👨‍🍳 Chef
- 🩺 Doctor
- 🏦 Bank Teller
- 📱 Influencer
- 🚫 Jobless

💡 Work to earn money and level up
⚠️ Overworking can damage your needs

---

### 🏦 Banking & Loans

Money types:
- 💵 Simcoin (cash)
- 🏦 Bank deposit

Available actions:
- Deposit
- Withdraw
- Apply Loan
- Repay Loan

⚠️ Loans help early game but can backfire if ignored

---

### 🏡 Assets

- 🚗 Car → reduces travel fatigue
- 🏠 House → unlocks essential home actions
- 🏨 Hotel → generates passive income

---

### 🐾 Pets

Adopt and care for pets:
- Feed 🍖
- Clean 🚿
- Play 🎾
- Sleep 😴

💡 Pets increase happiness but require upkeep

---

## 🌍 Locations

Explore different places:
- 🛣 Street
- 🏠 Home (requires house)
- 🌳 Park
- 🏦 Bank
- 🍽 Restaurant
- 🏥 Hospital
- 🐾 Pet Store
- 🎰 Casino

Each location offers different actions.

---

## 🎰 Casino

### 🚫 Restrictions
- Child Sims cannot enter

---

### 🎲 Slot Machine

- 🎰 777 → **25x payout**
- 🎰 Other triples → **10x**
- 🎰 Any pair → **2x**
- ❌ No match → lose bet

---

### 🃏 Blackjack (21)

- Start with 2 cards
- Options: Hit / Stand / Double
- Double = 1 extra card + auto stand

#### 💰 Payouts
- Natural blackjack → **3x**
- Normal win → **2x**
- Double-down win → **4x**
- Five-card Charlie (≤21) → instant win 🎉

---

## 💾 Save System

Your progress includes:
- ⏰ Time
- 👤 Sims & stats
- 💼 Jobs
- ❤️ Needs
- 🏦 Banking
- 🏡 Assets
- 🐾 Pets

📁 File: `savegame.txt`

---

## ▶️ How to Run

```bash
java simscli.Main