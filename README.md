# 🔎 Lost & Found – Campus Mystery Finder

A campus-focused web application built with **Spring Boot & Java (80%)** and **HTML5/CSS3/JavaScript (20%)**, designed to ensure **the right person recovers their belongings, not just anyone**.

---

## 🌟 How It Solves the 8 Conditions

| Condition | Requirement | Implementation in Project |
|---|---|---|
| **Condition 8** | Entry page asks Student or Admin Login | `/login` features an interactive tab gateway with dual portals: **🎓 Student Portal** and **🛡️ Admin Portal**. |
| **Condition 7** | Student logs in with email; Admin logs in with email & can create password | Students sign in via campus email; Admins log in via email & password, or create/reset an admin password at `/admin/setup` using the security key (`CAMPUS-ADMIN-2026`). |
| **Condition 1** | Homepage prompts: *"Did you lose or find something on campus?"* | Post-login home screen displays the dual action cards: **"I FOUND SOMETHING"** (Point 2 & 3) and **"I LOST SOMETHING"** (Point 4 & 5). |
| **Condition 2** | Camera access to take photo or import from gallery media | On `/report/found`, users can click **"📷 Open Device Camera"** for a live video viewfinder with a **"📸 Snap Photo"** button (Canvas capture), or **"🖼️ Choose from Gallery / Files"** with instant preview. |
| **Condition 3** | Object details: where found, colour, small or big, electronic or not | Form includes fields for: **Campus Location**, **Colour**, **Size** (Small, Medium, Big), **Is Electronic** (Yes / No), Category, Brand, and a **Secret Mystery Clue** (kept confidential from public). |
| **Condition 4** | Lost person searches product name | On `/search`, students search by name (e.g. `earbuds`, `bottle`, `calculator`, `keys`, `bag`) with instant category filtering. Found items are displayed with masked/blurred mystery cards to prevent visual copying. |
| **Condition 5** | Quiz lost person about object details | Clicking **"Verify & Claim"** on an item asks: 1) Where lost? 2) Colour? 3) Small or big? 4) Is it electronic? 5) Brand? 6) Secret unique markings/details? 7) Optional ownership proof. |
| **Condition 6** | Anti-fraud: only the right person gets their thing | Confidential details are hidden from public. Java's `MatchingEngineService` scores the claimant's answers against finder records. Campus admins review side-by-side on `/admin/dashboard` before issuing an **Official Campus Pickup Pass Code** (e.g. `PASS-CAMPUS-7924`). |

---

## 🏗️ Architecture & Technology Stack

- **80% Java Backend**:
  - `com.campus.lostfound.model`: Domain entities (`User`, `Item`, `Claim`).
  - `com.campus.lostfound.repository`: Spring Data JPA interfaces (`UserRepository`, `ItemRepository`, `ClaimRepository`).
  - `com.campus.lostfound.service`: Business services (`MatchingEngineService`, `UserService`, `ItemService`, `ClaimService`, `DataInitializer`).
  - `com.campus.lostfound.controller`: Web controllers (`AuthController`, `HomeController`, `ItemController`, `ClaimController`, `AdminController`).
  - `com.campus.lostfound.config`: Security & media config (`AuthInterceptor`, `WebConfig`).
  - Persistent H2 / MySQL database support with pre-seeded demo items.
- **20% Frontend**:
  - Modern Thymeleaf templates with responsive CSS (`style.css`).
  - Real-time camera viewfinder & canvas capture (`camera.js`).
  - Interactive portal tabs, chips, and dynamic forms (`script.js`).

---

## 🚀 How to Run the Application

### Option 1: Fast One-Click Launch (Recommended)
Simply double-click or run:
```cmd
run.cmd
```
or in PowerShell:
```powershell
.\run.ps1
```

### Option 2: Using the Included Maven Wrapper
```cmd
.\mvnw.cmd spring-boot:run
```

Once started, open your browser and go to:
👉 **http://localhost:8080**

---

## 🔑 Pre-Seeded Credentials for Testing

### 1. Student Portal
- **Email:** `student@campus.edu` (or any valid campus email)
- **Role:** Student (Can report found items, search items, submit claims, and track claim pass codes)

### 2. Administrator Portal
- **Email:** `admin@campus.edu`
- **Password:** `Admin@123`
- **Role:** Campus Authority Administrator (Can review claims side-by-side, approve/reject claims, issue pickup pass codes, and manage item inventory)
- **Admin Setup Key:** `CAMPUS-ADMIN-2026` (Use at `/admin/setup` to create new admin credentials)

### 3. Pre-Seeded Mystery Items Ready on the Board:
1. **Boat Airdopes Wireless Earbuds** (Found at Central Library 2nd Floor)
2. **Milton Stainless Steel Insulated Bottle** (Found at Student Food Court)
3. **Casio fx-991EX ClassWiz Calculator** (Found at Engineering Block B)
4. **Wildcraft Outdoor Backpack** (Found at Sports Complex Gymnasium)
