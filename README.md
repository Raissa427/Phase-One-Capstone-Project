# 💛 IgirePay Payment Gateway

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.13-C71A36?style=for-the-badge&logo=apachemaven)

> A secure desktop-based digital wallet system inspired by MTN MoMo Rwanda.
> Built as Backend Phase 1 Capstone — SheCanCODE Program by Igire Rwanda Organization.

## 📌 About

IgirePay simulates a real-world Mobile Money platform where users can manage wallets, send money, handle savings, request loans and view transaction history — secured with PIN authentication and duplicate transaction prevention.

## ✨ Key Features

- 🔐 PIN login with SHA-256 hashing and account locking after 3 failed attempts
- 💰 Deposit, withdraw and transfer money between users
- 🏦 Savings account management and loan requests
- 🔒 Idempotency protection — duplicate transactions automatically rejected
- 📊 Transaction history with CSV export
- 🛡️ Admin panel to manage customers and accounts
- 👥 Role-based access — Admin and User

## 🏗️ Architecture

```
UI → Service → DAOImpl → PostgreSQL
```

| Layer | Purpose |
|---|---|
| `lab1/model` | OOP classes — inheritance and polymorphism |
| `lab2/dao` | DAO interfaces and DAOImpl with PreparedStatements |
| `lab2/service` | Business logic layer |
| `lab3/exception` | Custom exception handling |
| `lab3/ui` | JavaFX screens |

## 🛠️ Tech Stack

Java 21 · JavaFX 21 · PostgreSQL · JDBC · OpenCSV · Maven · Git

## 🚀 Setup

**1. Clone the repo**
```bash
git clone https://github.com/yourusername/IgirePaymentManagementSystem.git
```

**2. Create database**
```sql
CREATE DATABASE igirepay_db;
```
Then run the full schema from `database/schema.sql`

**3. Update your password in**
`src/main/java/com/igirepay/lab2/db/DatabaseConnection.java`

**4. Load Maven dependencies**

Click the Maven refresh button in IntelliJ

**5. Run**
```bash
mvn clean javafx:run
```

**6. Create your first Admin**
```sql
UPDATE customers SET role = 'ADMIN' WHERE phone_number = 'your_phone_here';
```

## 🗄️ Database Schema

```sql
customers          → user accounts, PIN hash, role, lock status
accounts           → wallet and savings per customer
transactions       → all financial transactions with reference ID
processed_requests → reference IDs for idempotency protection
```

## 🎓 Academic Context

Built for the **SheCanCODE Backend Phase 1 Capstone** at Igire Rwanda Organization.

Demonstrates: OOP · JDBC · DAO Pattern · Service Layer · JavaFX · Git Workflow

## 🙏 Acknowledgements

- Igire Rwanda Organization and SheCanCODE instructors
- MTN Rwanda MoMo — inspiration for the system design

*Built with Raissa 💛 in Kigali, Rwanda — Igire Rwanda Organization*
```


