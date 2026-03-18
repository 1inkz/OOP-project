# 🎮 THE SIMS – TERMINAL CONSOLE EDITION

A Java-based terminal life simulation game where you manage Sims, balance their needs, earn money, buy assets, adopt pets, and try not to accidentally ruin their lives.

---

## 🧠 Overview

Welcome to a **SIMS survival simulator**.

In this game, you will manage:
- 🧍 Sims and their daily lives
- 🍔 Needs (yes… all of them)
- 💼 Jobs and income
- 💳 Banking and loans
- 🏠 Assets (Car, House, Hotel)
- 🐶 Pets (because why not make life harder)
- ⏰ Time (your biggest enemy)

👉 Goal: **Stay alive, stay stable, and don’t go broke.**

---

## 🔥 Core Systems

### 🧍 Sim System
Each Sim has:
- needs
- job & progression
- finances
- assets
- pets
- location

You can create multiple Sims, but control one at a time.

---

### 🍔 Needs System (DO NOT IGNORE THIS)

- Hunger
- Energy
- Hygiene
- Social
- Fun
- Bladder

💀 If ANY hits **0 → your Sim dies, so watch out!**

| Level | Status |
|------|--------|
| 60+  | 🟢 Good |
| 21–59| 🟡 Warning |
| 0–20 | 🔴 Critical |

---

### ⏰ Time System (THE REAL BOSS)

- Every action ≈ 1 hour
- Travel also costs time
- Time keeps moving → your needs keep dropping

🌙 Night system:
- 20:00 → 😴 Reminder
- 21:00 → ⚠️ Warning
- 22:00 → 💀 Forced sleep / faint

👉 Ignore time → you lose control fast

---

### 💼 Jobs & Income

Jobs available:
- 👨‍🍳 Chef
- 🏥 Doctor
- 🏦 Bank Teller
- 📱 Influencer
- 🚫 Jobless

💰 Work → earn money  
📈 Work more → earn better

BUT:
👉 Work too much → your needs crash

---

### 💳 Banking & Loans

Money is split into:
- 💵 Simcoin (on hand)
- 🏦 Bank balance

You can:
- deposit
- withdraw
- take loans
- repay loans

⚠️ Loans = helpful but dangerous  
Ignore them → repossession → death risk

---

### 🏠 Assets

Available:
- 🚗 Car → better travel
- 🏠 House → unlock survival actions
- 🏨 Hotel → passive income

💡 Beginner tip:
👉 House > everything else early game

---

### 🐶 Pets System

Yes… you can make life harder:

- buy pets 🐾
- feed 🍖
- clean 🧼
- play 🎾
- sleep together 😴

⚠️ Pets:
- need care
- age
- can die

👉 Don’t get pets if you’re already struggling 😅

---

### 🌍 Locations

- 🚶 Street
- 🏠 Home (requires house!)
- 🌳 Park
- 🏦 Bank
- 🍽 Restaurant
- 🏥 Hospital
- 🐶 Pet Store

Different places = different actions

---

## 🕹️ Menu System

### Start Menu
- New Game
- Continue Game
- Quit

### Sims Menu
- Create Sim
- Select Sim
- Enter Game

### Main Menu
- View Status 👀
- Travel ✈️
- Location Actions 📍
- Job Management 💼
- Assets 🏠
- Pass Time ⏰

---

## 💾 Save System

Game saves:
- time ⏰
- Sims 🧍
- jobs 💼
- needs 🍔
- money 💰
- assets 🏠
- pets 🐶

📄 Saved in: `savegame.txt`

---

## 🚀 How to Run

```bash
java simscli.Main