# 🏦 Banking Management System (Console-Based)

## 📘 Overview
The **Banking Management System** is a console-based Java application developed to simulate core banking functionalities such as **account creation, login, deposit, withdrawal, and balance enquiry**.  
It uses **JDBC** to connect with a **MySQL database**, ensuring secure and persistent data storage.

This project demonstrates the use of **Object-Oriented Programming (OOP)** concepts, **Exception Handling**, and **Database Connectivity (JDBC)** in Java.

---

## 🧠 Features
- ✅ Secure **login** and **account registration**
- 💰 **Deposit**, **Withdrawal**, and **Balance Enquiry** operations
- 🔐 **Password authentication** and **input validation**
- ⚙️ **MySQL database integration** using JDBC
- 🧾 Validation for **minimum balance** and prevention of **negative withdrawals**
- 📂 Modular code structure with proper business logic

---

## 🏗️ Technologies Used
| Category | Technology |
|-----------|-------------|
| Programming Language | Java (JDK 17+) |
| Database | MySQL 8+ |
| Connectivity | JDBC (Java Database Connectivity) |
| IDE | VS Code / Eclipse / IntelliJ |
| Build Tool | None (simple Java execution) |

---

## 🗄️ MySQL Database Schema

-- create database
CREATE DATABASE IF NOT EXISTS bankdb;
USE bankdb;

-- accounts table
CREATE TABLE IF NOT EXISTS accounts (
  account_number BIGINT PRIMARY KEY,     -- user provided account number
  name VARCHAR(100) NOT NULL,
  password_hash CHAR(64) NOT NULL,       -- SHA-256 hex (64 chars)
  mobile VARCHAR(20),
  email VARCHAR(100),
  balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- optional: sample account for testing
INSERT INTO accounts(account_number, name, password_hash, mobile, email, balance)
VALUES (123456, 'Test User', 'ef92b778bafe771e89245b89ecbc9b36b3f2b3f7f5a0f9a5b8d5f6e7a8b9c0d1', '9999999999', 'test@example.com', 1000.00);
-- Note: password_hash above is placeholder; generate properly or register through app.

⚙️ How to Run the Project
1️⃣ Clone the Repository
git clone https://github.com/sasikumar57/BankingApp.git
cd BankingApp

2️⃣ Set Up Database

Open MySQL Workbench or terminal.

Run the SQL commands in banking_schema.sql (or from above).

3️⃣ Update Database Credentials

In BankingApp.java, modify these lines according to your local MySQL setup:
private static final String DB_URL = "jdbc:mysql://localhost:3306/besant";
private static final String DB_USER = "root";
private static final String DB_PASS = "your_password";

4️⃣ Compile and Run
javac BankingApp.java
java BankingApp

Example Operations : 
Deposit / Withdrawal / Balance
1. Deposit
2. Withdraw
3. Check Balance
4. Exit
Enter choice: 1
Enter amount: 1000
Deposit successful! Updated balance: ₹6000.00

🧩 Concepts Implemented

Java OOP (Classes, Objects, Methods)

Loops & Conditional Statements

Exception Handling

JDBC Connection Handling

SQL CRUD Operations

Data Validation & Input Constraints

📜 Future Enhancements

Add transaction history for each account

Encrypt passwords using hashing

Introduce role-based admin dashboard

Build a GUI version using JavaFX or Swing

👨‍💻 Author

Sasikumar M
📧 sasikumarsk5731@gmail.com

🌐 github.com/sasikumar57

💼 linkedin.com/in/sasikumar-m-453807219
