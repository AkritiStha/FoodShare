# FoodShare – Setup & Deployment Guide

**CS5054NT Coursework | Java EE Web Application**

FoodShare is a food waste reduction platform built with Java 17+, Java EE (Servlets + JSP), MySQL 8, and Apache Tomcat 9/10. It connects food **Donors** with **NGOs** through a role-based MVC web application.

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java JDK | 17 or 21 | `java -version` to check |
| Apache Tomcat | 9.0.x or 10.1.x | Download from tomcat.apache.org |
| MySQL Server | 8.0+ | Running locally on port 3306 |
| IDE (optional) | IntelliJ IDEA / Eclipse / VS Code | Any IDE with Tomcat support |

---

## Required JAR Files

Place all JARs in `web/WEB-INF/lib/`:

| JAR | Download Source | Purpose |
|-----|----------------|---------|
| `mysql-connector-j-8.x.x.jar` | [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) | JDBC driver |
| `jbcrypt-0.4.jar` | [jBCrypt on Maven Central](https://mvnrepository.com/artifact/org.mindrot/jbcrypt/0.4) | BCrypt password hashing |
| `jakarta.servlet.jsp.jstl-2.0.0.jar` | [Maven Central](https://mvnrepository.com/artifact/org.glassfish.web/jakarta.servlet.jsp.jstl) | JSTL tag library |
| `jakarta.servlet.jsp.jstl-api-2.0.0.jar` | Maven Central | JSTL API |

> **Tomcat 9** uses `javax.*` packages. **Tomcat 10+** uses `jakarta.*` packages.  
> The source code uses `jakarta.*`. If using Tomcat 9, replace all `jakarta.servlet` imports with `javax.servlet`.

---

## Step 1 – Database Setup

1. Start MySQL and log in:
   ```bash
   mysql -u root -p
   ```

2. Run the schema script:
   ```sql
   source /path/to/FoodShare/foodshare.sql;
   ```
   Or via MySQL Workbench: **File → Run SQL Script → select `foodshare.sql`**

3. Verify:
   ```sql
   USE foodshare;
   SHOW TABLES;
   SELECT name, email, role FROM users;
   ```

---

## Step 2 – Configure Database Credentials

Edit `web/WEB-INF/classes/db.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/foodshare?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD_HERE
```

> This file must end up on the classpath. When deploying to Tomcat it should be at:  
> `WEB-INF/classes/db.properties`

---

## Step 3 – Project Structure

```
FoodShare/
├── foodshare.sql                  ← Database schema + sample data
├── README.md
├── src/
│   ├── model/                     ← Domain objects (User, FoodItem, Request, …)
│   ├── dao/                       ← JDBC data access (UserDAO, FoodItemDAO, …)
│   ├── service/                   ← Business logic (UserService, FoodService, …)
│   ├── controller/                ← Servlets (LoginServlet, AddFoodServlet, …)
│   ├── filter/                    ← AuthenticationFilter, RoleFilter
│   └── util/                      ← DBConnection, PasswordUtil, ValidationUtil, DistanceCalculator
└── web/
    ├── index.jsp                  ← Root redirect
    ├── WEB-INF/
    │   ├── web.xml                ← Deployment descriptor
    │   ├── lib/                   ← ← Place JARs here
    │   └── classes/
    │       └── db.properties      ← DB credentials
    ├── css/style.css              ← Full responsive stylesheet
    ├── js/script.js               ← Minimal vanilla JS
    ├── common/                    ← login, register, profile, about, contact, navbar
    ├── donor/                     ← Donor JSP pages
    ├── ngo/                       ← NGO JSP pages
    ├── admin/                     ← Admin JSP pages
    └── error/                     ← 403, 404, 500 pages
```

---

## Step 4 – Build & Deploy

### Option A: IntelliJ IDEA

1. Open the project as a **Web Application** (or create a new one and add sources).
2. Add an **Artifact**: `Web Application: Exploded` pointing to `web/`.
3. Add source root: `src/` with output to `web/WEB-INF/classes/`.
4. Configure **Tomcat Server** → point to your Tomcat installation.
5. Add the artifact to the deployment tab.
6. Ensure all JARs are in `web/WEB-INF/lib/`.
7. Click **Run**.

### Option B: Eclipse (Dynamic Web Project)

1. **File → New → Dynamic Web Project** named `FoodShare`.
2. Copy `src/` files into `src/` and `web/` into `WebContent/`.
3. Place JARs in `WebContent/WEB-INF/lib/`.
4. Right-click project → **Run As → Run on Server** (select Tomcat).

### Option C: Manual WAR deployment

1. Compile all Java sources:
   ```bash
   javac -cp "WEB-INF/lib/*:tomcat/lib/servlet-api.jar" \
         -d web/WEB-INF/classes \
         src/**/*.java
   ```
2. Package as WAR:
   ```bash
   cd web
   jar -cvf ../FoodShare.war .
   ```
3. Copy `FoodShare.war` to `$TOMCAT_HOME/webapps/`.
4. Start Tomcat: `$TOMCAT_HOME/bin/startup.sh` (or `.bat` on Windows).
5. Access: [http://localhost:8080/FoodShare](http://localhost:8080/FoodShare)

---

## Step 5 – Test Accounts

All sample accounts use the password: **`Password1!`**

| Role | Email | Password | Notes |
|------|-------|----------|-------|
| Admin | `admin@foodshare.com` | `Password1!` | Full platform access |
| Donor | `greenleaf@donor.com` | `Password1!` | Green Leaf Restaurant |
| Donor | `cityhotel@donor.com` | `Password1!` | City Hotel Kitchen |
| NGO | `hope@ngo.com` | `Password1!` | Hope Shelter (approved) |
| NGO | `community@ngo.com` | `Password1!` | Community Kitchen (approved) |

---

## Application URLs

| URL | Description |
|-----|-------------|
| `/FoodShare/login` | Login page |
| `/FoodShare/register` | Registration page |
| `/FoodShare/donor/dashboard` | Donor dashboard |
| `/FoodShare/donor/addFood` | Add food listing |
| `/FoodShare/donor/myListings` | View/edit/delete listings |
| `/FoodShare/donor/requests` | View & respond to NGO requests |
| `/FoodShare/ngo/dashboard` | NGO dashboard |
| `/FoodShare/ngo/searchFood` | Search available food by location |
| `/FoodShare/ngo/myRequests` | NGO request history + rating |
| `/FoodShare/admin/dashboard` | Admin overview |
| `/FoodShare/admin/manageUsers` | Approve / delete users |
| `/FoodShare/admin/manageFood` | View / delete food listings |
| `/FoodShare/admin/reports` | Platform reports & metrics |
| `/FoodShare/about` | About FoodShare |
| `/FoodShare/contact` | Contact page |

---

## Key Features

- **BCrypt password hashing** (salt rounds = 10) — no plain-text passwords stored
- **Session management** — 30-minute timeout, session invalidated on logout
- **Authentication filter** — all pages require login except public routes
- **Role-based access control** — donors/NGOs/admin cannot cross-access each other's pages
- **Haversine distance formula** — NGO food search sorted by distance (nearest first)
- **Auto-expiry** — food items past their expiry date are automatically hidden from NGO search
- **In-app notifications** — donors notified on new requests; NGOs notified on acceptance/rejection/completion
- **Star ratings** — NGOs rate completed donations (1–5 stars)
- **SQL injection prevention** — all queries use `PreparedStatement`
- **Responsive design** — custom CSS with Flexbox + media queries (mobile / tablet / desktop)
- **MVC architecture** — clean separation: model → DAO → service → controller → JSP

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Place `mysql-connector-j.jar` in `WEB-INF/lib/` |
| `ClassNotFoundException: org.mindrot.jbcrypt.BCrypt` | Place `jbcrypt-0.4.jar` in `WEB-INF/lib/` |
| `Access denied for user 'root'@'localhost'` | Update `db.properties` with correct MySQL password |
| `Table 'foodshare.users' doesn't exist` | Run `foodshare.sql` in MySQL first |
| 404 on all servlet URLs | Check `web.xml` servlet mappings match annotation `@WebServlet` paths |
| `jakarta.servlet` not found (Tomcat 9) | Replace `jakarta.servlet` with `javax.servlet` in all Java files |
| Session not persisting | Check `WEB-INF/web.xml` session-config block is present |
| Geolocation not working | Browser requires HTTPS for `navigator.geolocation` in production |

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17/21 |
| Web Framework | Java EE — Servlets + JSP |
| Database | MySQL 8.0 |
| DB Access | JDBC (PreparedStatement) |
| Server | Apache Tomcat 9 / 10 |
| Password Security | BCrypt (jBCrypt 0.4) |
| Frontend | JSP + Custom CSS (Flexbox, Media Queries) |
| JS | Vanilla JavaScript (UX only) |
| Architecture | MVC (Model–View–Controller) |

---

*FoodShare — CS5054NT Advanced Web Technologies Coursework*
