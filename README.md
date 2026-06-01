# GHADS - Gaza Humanitarian Aid Distribution System 🌍

### 📌 Project Overview
**GHADS** (Gaza Humanitarian Aid Distribution System) is a centralized desktop enterprise application

The system is engineered to help humanitarian organizations operating within the Gaza Strip coordinate their aid distribution campaigns efficiently and transparently, ensuring that limited resources are allocated to displaced families with optimal equity and integrity.

---

### 🛑 The Problem It Solves
During humanitarian crises, numerous independent non-governmental organizations (NGOs), UN agencies, and local committees distribute vital aid packages simultaneously. When these organizations operate in isolation, several systemic issues arise:
* **Aid Overlap & Resource Duplication:** The exact same displaced family might accidentally receive identical aid packages from multiple organizations within a short period, leaving other equally desperate families completely unserved.
* **Fragmentation of Beneficiary Tracking:** Lack of a synchronized database leads to inconsistent records regarding household sizes, dynamic geographical locations (camps/shelters), and current vulnerability status.
* **Disparities in Distribution:** No clear analytical mechanism to instantly extract lists of registered families who have been entirely left out of recent distribution cycles.

**GHADS Centralized Solution:**
By offering a shared database infrastructure, GHADS enforces systematic coordination. It integrates a rule-based engine that evaluates dynamic criteria before any distribution log is saved, blocking premature duplicate aid for families with stable or moderate profiles while prioritizing high-urgency cases to optimize field impact.

---

### 🛠️ Technologies & Tools Used
The application implements modern enterprise Java development paradigms:
* **Core Language:** Java (Object-Oriented Programming, Java Streams, and Lambdas for data filtering)
* **Graphical User Interface:** JavaFX paired with Scene Builder for building native desktop components
* **Styling & Themes:** Custom CSS (supporting fluid UI styling, layout consistency, and Dark/Light Mode themes)
* **Persistence Layer (ORM):** Java Persistence API (JPA) / Hibernate for mapping relational schemas to object models
* **Database Management System:** MySQL Server for relational data architecture
* **Project Management:** Maven for dependency tracking and lifecycle automation

---

### 📐 Architectural & Design Patterns

#### 1. Model-View-Controller (MVC) Architecture
The project decouples UI layers from foundational business data to maximize extensibility:
* **Model Layer (`models` package):** Defines system entities mapped directly to MySQL tables (e.g., `User`, `Organization`, `Family`, `AidDistribution`) using standard JPA annotations (`@Entity`, `@Table`, `@ManyToOne`, `@JoinColumn`).
* **View Layer (`views` and `styles` packages):** Formulated utilizing declarative `.fxml` views designed in Scene Builder alongside custom CSS properties that enforce typographic controls, responsive grid behaviors, and color scheme toggles.
* **Controller Layer (`controllers` package):** Intercepts client actions from UI views, executes state mutations, handles operational input validations, and pushes updates back onto the presentation layout.

#### 2. Data Access Object (DAO) Pattern
To isolate persistence logic from UI controllers, the architecture defines dedicated DAO components (e.g., `UserDAO`, `FamilyDAO`, `AidDistributionDAO`). These manage standard CRUD transactions, control `EntityManager` scoping, and write highly optimized JPQL queries, ensuring application-wide data isolation and mitigating connection resource leaks.

#### 3. Singleton Pattern
Integrated within the configuration modules (`config` package) to maintain a singular global configuration instance and control state contexts such as current user sessions or localized DB factory allocations.

---

### ⚙️ Core Logic: Duplicate Aid Check Implementation
The system handles duplicate prevention via a dedicated backend validation transaction inside `AidDistributionDAO`:

1. **Vulnerability Evaluation:** Upon registering an aid package, the system checks the recipient family's `vulnerabilityLevel` (HIGH, MEDIUM, or LOW).
2. **JPQL Historic Sub-Query:** For `MEDIUM` or `LOW` categories, a JPQL query scans the `AidDistribution` history table filtered by `family_id`, ordered descending by date, and constrained to a maximum result of 1 (`.setMaxResults(1)`) to pull only the single most recent record.
3. **Temporal Constraint Calculation:** The system evaluates the chronological gap between the target date and the past distribution via `ChronoUnit.DAYS.between()`.
4. **Exception Escalation:** If the gap evaluates to **less than 30 days**, the transaction aborts via a custom `DuplicateAidException`. This halts database persistence and routes a package of contextual data (Family Name, Level, Prior Granting Organization, and Exact Date) directly to the UI layer to produce a structured alert dialogue. Families flagged as `HIGH` vulnerability bypass this block to receive immediate life-saving support.

---

### 🗃️ Database Schema Architecture
The underlying database design consists of 4 main relational entities structured to handle full integrity constraints:
* **Organization:** Tracks operational entity profiles (`org_id` [PK], `name`, `type` [NGO/UN/Local], `contact_info`).
* **User:** Enforces Role-Based Access Control (`user_id` [PK], `username` [Unique], `password`, `full_name`, `email`, `role` [ADMIN / COORDINATOR], `org_id` [FK]).
* **Family:** Catalogs vulnerable displacement units (`family_id` [PK], `household_name`, `phone`, `location`, `family_size`, `national_id` [Unique], `vulnerability_level`, `registration_date`, `last_aid_date`).
* **AidDistribution:** Maps the intersecting transactional ledger linking beneficiaries to donors (`distribution_id` [PK], `family_id` [FK], `org_id` [FK], `distributed_by` [FK - User], `distribution_date`).

---

### 📸 Application System Screenshots

<details>
<summary><b>🖼️ Click here to watch all screenshots of my system</b></summary>
<br>

![Login Screen](images/screenshots/1login.png)
![Admin Login Screen](images/screenshots/1loginAdmin.png)
![Admin Dashboard](images/screenshots/2AdminMainDashboard.png)
![Admin User Management](images/screenshots/3AdminUserManagment.png)
![Admin Add User](images/screenshots/4AdminAddUser.png)
![Admin Organization](images/screenshots/5AdminOrganization.png)
![Admin Add Organization](images/screenshots/6AdminAddOrganization.png)
![Admin Family Management](images/screenshots/7AdminFamilyMaangemnt.png)
![Admin New Family](images/screenshots/8AdminNewFamily.png)
![Admin Aid Distribution](images/screenshots/9AdminAidDistribution.png)
![Admin Change Password](images/screenshots/10AdminChangePassword.png)
![Coordinator Dashboard](images/screenshots/11CoordMainDashboard.png)
![Coordinator Profile](images/screenshots/12CoordProfile.png)
![Coordinator Family Management](images/screenshots/13CoordFamilyManagment.png)
![Coordinator Add New Family](images/screenshots/14CoordAddNewFamily.png)
![Coordinator Aid Distribution](images/screenshots/15CoordAidDistribution.png)
![Coordinator Add Aid Distribution](images/screenshots/16CoordAddAidDistributeion.png)
![Coordinator Change Password](images/screenshots/17CoordChangePassword.png)
![System Dark Mode](images/screenshots/18SystemDarkMode.png)
![About Screen](images/screenshots/About.png)

</details>

---

### 🚀 Getting Started

1. **Prerequisites:** Ensure you have JDK 11+ and MySQL Server configured.
2. **Database Configuration:** Set up your database parameters within the `META-INF/persistence.xml` configuration file.
3. **Execution:** Open the root directory as an existing project inside **NetBeans IDE** and hit **Run (F6)**, or compile using the `build.xml` file.
