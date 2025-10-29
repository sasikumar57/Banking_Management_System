package bankProject;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class BankingApp {

    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Sasi@2001";

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to Simple BankingApp ===");
        while (true) {
            showStartMenu();
        }
    }

    // Start menu: 1. Login 2. Register 3. Exit
    private static void showStartMenu() {
        System.out.println("\nChoose an option (enter option number only):");
        System.out.println("1. Login with account number");
        System.out.println("2. Register new account");
        System.out.println("3. Exit");

        String option = sc.nextLine().trim();

        switch (option) {
            case "1":
                loginFlow();
                break;
            case "2":
                registerFlow();
                break;
            case "3":
                exitApp();
                break;
            default:
                System.out.println("Invalid option! Please enter 1, 2 or 3.");
                // Loop returns to start menu
        }
    }

    // Login flow
    private static void loginFlow() {
        System.out.print("Enter account number: ");
        String accStr = sc.nextLine().trim();
        long accNum;
        try {
            accNum = Long.parseLong(accStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid account number format. Returning to main menu.");
            return;
        }

        if (!isAccountExists(accNum)) {
            System.out.println("Account number not found. Returning to main menu.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String hashed = hashPassword(password);

        if (validatePassword(accNum, hashed)) {
            System.out.println("Login successful. Welcome!");
            authenticatedMenu(accNum);
        } else {
            System.out.println("Incorrect password. Returning to main menu.");
        }
    }

    // Register flow
    private static void registerFlow() {
        System.out.println("Register new account - please provide details.");

        System.out.print("Customer name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty. Registration cancelled.");
            return;
        }

        System.out.print("Account number (numbers only): ");
        String accStr = sc.nextLine().trim();
        long accNum;
        try {
            accNum = Long.parseLong(accStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid account number format. Registration cancelled.");
            return;
        }

        if (isAccountExists(accNum)) {
            System.out.println("Account number already exists. Choose a different account number.");
            return;
        }

        System.out.print("Password: ");
        String password = sc.nextLine();
        if (password.length() < 4) {
            System.out.println("Password too short (min 4 chars). Registration cancelled.");
            return;
        }

        System.out.print("Mobile number: ");
        String mobile = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Initial account balance (numbers only): ");
        String balStr = sc.nextLine().trim();
        double initialBalance;
        try {
            initialBalance = Double.parseDouble(balStr);
            if (initialBalance < 0) {
                System.out.println("Initial balance cannot be negative. Registration cancelled.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Registration cancelled.");
            return;
        }

        String hashed = hashPassword(password);

        boolean created = createAccount(accNum, name, hashed, mobile, email, initialBalance);
        if (created) {
            System.out.println("Account registered successfully! You can now login.");
        } else {
            System.out.println("Failed to create account. Try again later.");
        }
    }

    // Authenticated menu for logged-in user
    private static void authenticatedMenu(long accountNumber) {
        while (true) {
            System.out.println("\nSelect the Option (enter option number only):");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Show Balance");
            System.out.println("4. Logout");

            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    depositFlow(accountNumber);
                    break;
                case "2":
                    withdrawFlow(accountNumber);
                    break;
                case "3":
                    showBalance(accountNumber);
                    break;
                case "4":
                    System.out.println("Logging out...");
                    return; // back to start menu
                default:
                    System.out.println("Invalid option! Please enter 1, 2, 3 or 4.");
            }
        }
    }

    // Deposit implementation
    private static void depositFlow(long accountNumber) {
        System.out.print("Enter the amount to deposit: ");
        String amtStr = sc.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                System.out.println("Deposit amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }

        boolean success = updateBalance(accountNumber, amount, true);
        if (success) System.out.println("Amount deposited successfully.");
        else System.out.println("Deposit failed. Try again later.");
    }

    // Withdraw implementation with overdraft prevention
    private static void withdrawFlow(long accountNumber) {
        System.out.print("Enter the amount to withdraw: ");
        String amtStr = sc.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtStr);
            if (amount <= 0) {
                System.out.println("Withdrawal amount must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }

        double currentBalance = getBalance(accountNumber);
        if (currentBalance < 0) {
            System.out.println("Account has an inconsistent balance. Contact support.");
            return;
        }

        if (amount > currentBalance) {
            System.out.println("Insufficient balance. Withdrawal denied.");
            return;
        }

        boolean success = updateBalance(accountNumber, amount, false);
        if (success) System.out.println("Amount withdrawn successfully.");
        else System.out.println("Withdrawal failed. Try again later.");
    }

    // Show balance
    private static void showBalance(long accountNumber) {
        double bal = getBalance(accountNumber);
        if (bal >= 0) {
            System.out.printf("Account number: %d%n", accountNumber);
            System.out.printf("Total Balance: %.2f%n", bal);
        } else {
            System.out.println("Unable to retrieve balance at this time.");
        }
    }

    // Exit the application
    private static void exitApp() {
        System.out.println("Goodbye!");
        sc.close();
        System.exit(0);
    }

    // ========== Database utility methods ==========

    // check existence of account
    private static boolean isAccountExists(long accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, accountNumber);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error when checking account existence: " + e.getMessage());
            return false;
        }
    }

    // validate password
    private static boolean validatePassword(long accountNumber, String hashedPassword) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, accountNumber);
            pst.setString(2, hashedPassword);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Database error when validating password: " + e.getMessage());
            return false;
        }
    }

    // create account
    private static boolean createAccount(long accNum, String name, String passwordHash, String mobile, String email, double initialBalance) {
        String sql = "INSERT INTO accounts(account_number, name, password_hash, mobile, email, balance) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, accNum);
            pst.setString(2, name);
            pst.setString(3, passwordHash);
            pst.setString(4, mobile);
            pst.setString(5, email);
            pst.setDouble(6, initialBalance);
            int rows = pst.executeUpdate();
            return rows == 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Account number already exists.");
            return false;
        } catch (SQLException e) {
            System.out.println("Database error when creating account: " + e.getMessage());
            return false;
        }
    }

    // get balance
    private static double getBalance(long accountNumber) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, accountNumber);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                } else {
                    return -1.0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error when fetching balance: " + e.getMessage());
            return -1.0;
        }
    }

    // update balance: deposit (isDeposit=true) or withdraw (isDeposit=false)
    // Uses a transaction to ensure atomic update
    private static boolean updateBalance(long accountNumber, double amount, boolean isDeposit) {
        String selectSql = "SELECT balance FROM accounts WHERE account_number = ? FOR UPDATE";
        String updateSql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            conn.setAutoCommit(false);

            try (PreparedStatement pstSel = conn.prepareStatement(selectSql)) {
                pstSel.setLong(1, accountNumber);
                try (ResultSet rs = pstSel.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.out.println("Account not found.");
                        return false;
                    }
                    double currentBalance = rs.getDouble("balance");
                    double newBalance = isDeposit ? currentBalance + amount : currentBalance - amount;

                    // prevent overdraft
                    if (!isDeposit && newBalance < 0) {
                        System.out.println("Insufficient balance. Operation aborted.");
                        conn.rollback();
                        return false;
                    }

                    try (PreparedStatement pstUpd = conn.prepareStatement(updateSql)) {
                        pstUpd.setDouble(1, newBalance);
                        pstUpd.setLong(2, accountNumber);
                        int updated = pstUpd.executeUpdate();
                        if (updated == 1) {
                            conn.commit();
                            return true;
                        } else {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error when updating balance: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                // ignore
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    // ========== Utility: password hashing ==========
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}

