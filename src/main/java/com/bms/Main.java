package com.bms;

import com.bms.model.Account;
import com.bms.model.Customer;
import com.bms.model.Transaction;
import com.bms.service.AccountService;
import com.bms.service.AuthService;
import com.bms.service.TransactionService;
import com.bms.util.DatabaseConnection;
import com.bms.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Main class - Entry point for Banking Management System
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final AccountService accountService = new AccountService();
    private static final TransactionService transactionService = new TransactionService();
    private static Customer loggedInCustomer = null;
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public static void main(String[] args) {
        logger.info("Banking Management System started");
        
        // Test database connection
        if (!testDatabaseConnection()) {
            System.out.println("\n❌ Failed to connect to database. Please check your database configuration.");
            System.out.println("Make sure MySQL is running and db.properties is configured correctly.");
            return;
        }

        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║   BANKING MANAGEMENT SYSTEM              ║");
        System.out.println("║   Welcome to Secure Banking              ║");
        System.out.println("╚═══════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            try {
                if (loggedInCustomer == null) {
                    running = showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                logger.error("Unexpected error in main loop", e);
                System.out.println("\n❌ An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
        logger.info("Banking Management System terminated");
        System.out.println("\nThank you for using Banking Management System!");
    }

    /**
     * Test database connection
     */
    private static boolean testDatabaseConnection() {
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            return dbConn.testConnection();
        } catch (Exception e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }

    /**
     * Show login/registration menu
     */
    private static boolean showLoginMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("1. Login");
        System.out.println("2. Register New Customer");
        System.out.println("3. Exit");
        System.out.println("=".repeat(45));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    return false;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        }

        return true;
    }

    /**
     * Handle user login
     */
    private static void handleLogin() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("LOGIN");
        System.out.println("=".repeat(45));

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            loggedInCustomer = authService.login(username, password);
            System.out.println("\n✅ Login successful! Welcome, " + loggedInCustomer.getFullName() + "!");
            logger.info("User logged in: {}", username);
        } catch (Exception e) {
            System.out.println("\n❌ Login failed: " + e.getMessage());
            logger.warn("Login failed for username: {}", username);
        }
    }

    /**
     * Handle customer registration
     */
    private static void handleRegistration() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("CUSTOMER REGISTRATION");
        System.out.println("=".repeat(45));

        try {
            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Phone (10 digits): ");
            String phone = scanner.nextLine().trim();

            System.out.print("Address: ");
            String address = scanner.nextLine().trim();

            System.out.print("Username (3-20 alphanumeric): ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            // Validate password strength
            if (!PasswordUtil.isPasswordStrong(password)) {
                System.out.println("\n❌ Password does not meet requirements:");
                System.out.println(PasswordUtil.getPasswordRequirements());
                return;
            }

            System.out.print("Confirm Password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (!password.equals(confirmPassword)) {
                System.out.println("\n❌ Passwords do not match!");
                return;
            }

            // Hash password
            String passwordHash = PasswordUtil.hashPassword(password);

            // Create customer object
            Customer customer = new Customer(firstName, lastName, email, phone, address, username, passwordHash);

            // Register customer
            Long customerId = authService.register(customer);
            System.out.println("\n✅ Registration successful! Your Customer ID is: " + customerId);
            System.out.println("You can now login with your username and password.");
            
            logger.info("New customer registered: {}", username);

        } catch (Exception e) {
            System.out.println("\n❌ Registration failed: " + e.getMessage());
            logger.error("Registration failed", e);
        }
    }

    /**
     * Show main menu after login
     */
    private static void showMainMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("MAIN MENU - Welcome, " + loggedInCustomer.getFullName());
        System.out.println("=".repeat(45));
        System.out.println("1. Account Management");
        System.out.println("2. Banking Operations");
        System.out.println("3. Transaction History");
        System.out.println("4. Profile Management");
        System.out.println("5. Logout");
        System.out.println("=".repeat(45));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    showAccountManagementMenu();
                    break;
                case 2:
                    showBankingOperationsMenu();
                    break;
                case 3:
                    showTransactionHistory();
                    break;
                case 4:
                    showProfileManagement();
                    break;
                case 5:
                    handleLogout();
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        }
    }

    /**
     * Show account management menu
     */
    private static void showAccountManagementMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("ACCOUNT MANAGEMENT");
        System.out.println("=".repeat(45));
        System.out.println("1. Open New Account");
        System.out.println("2. View All My Accounts");
        System.out.println("3. View Account Details");
        System.out.println("4. Close Account");
        System.out.println("5. Back to Main Menu");
        System.out.println("=".repeat(45));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleOpenAccount();
                    break;
                case 2:
                    handleViewAllAccounts();
                    break;
                case 3:
                    handleViewAccountDetails();
                    break;
                case 4:
                    handleCloseAccount();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        }
    }

    /**
     * Handle opening a new account
     */
    private static void handleOpenAccount() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("OPEN NEW ACCOUNT");
        System.out.println("=".repeat(45));

        try {
            System.out.println("Select Account Type:");
            System.out.println("1. SAVINGS");
            System.out.println("2. CURRENT");
            System.out.println("3. FIXED_DEPOSIT");
            System.out.println("4. RECURRING_DEPOSIT");
            System.out.print("Enter choice: ");

            int typeChoice = Integer.parseInt(scanner.nextLine().trim());
            Account.AccountType accountType;

            switch (typeChoice) {
                case 1:
                    accountType = Account.AccountType.SAVINGS;
                    break;
                case 2:
                    accountType = Account.AccountType.CURRENT;
                    break;
                case 3:
                    accountType = Account.AccountType.FIXED_DEPOSIT;
                    break;
                case 4:
                    accountType = Account.AccountType.RECURRING_DEPOSIT;
                    break;
                default:
                    System.out.println("❌ Invalid account type");
                    return;
            }

            System.out.print("Initial Deposit Amount (0 or more): ");
            BigDecimal initialDeposit = new BigDecimal(scanner.nextLine().trim());

            Account account = accountService.openAccount(loggedInCustomer.getCustomerId(), 
                                                        accountType, initialDeposit);

            System.out.println("\n✅ Account opened successfully!");
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Initial Balance: " + currencyFormatter.format(account.getBalance()));

        } catch (Exception e) {
            System.out.println("\n❌ Failed to open account: " + e.getMessage());
            logger.error("Error opening account", e);
        }
    }

    /**
     * Handle viewing all accounts
     */
    private static void handleViewAllAccounts() {
        try {
            List<Account> accounts = accountService.getAccountsByCustomerId(loggedInCustomer.getCustomerId());

            if (accounts.isEmpty()) {
                System.out.println("\n📭 You don't have any accounts yet.");
                return;
            }

            System.out.println("\n" + "=".repeat(80));
            System.out.println("YOUR ACCOUNTS");
            System.out.println("=".repeat(80));
            System.out.printf("%-15s %-20s %-20s %-15s%n", "Account ID", "Account Number", "Type", "Balance");
            System.out.println("-".repeat(80));

            for (Account account : accounts) {
                System.out.printf("%-15d %-20s %-20s %-15s%n",
                        account.getAccountId(),
                        account.getAccountNumber(),
                        account.getAccountType(),
                        currencyFormatter.format(account.getBalance()));
            }
            System.out.println("=".repeat(80));

        } catch (Exception e) {
            System.out.println("\n❌ Failed to fetch accounts: " + e.getMessage());
            logger.error("Error fetching accounts", e);
        }
    }

    /**
     * Handle viewing account details
     */
    private static void handleViewAccountDetails() {
        System.out.print("\nEnter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            System.out.println("\n" + "=".repeat(45));
            System.out.println("ACCOUNT DETAILS");
            System.out.println("=".repeat(45));
            System.out.println("Account ID: " + account.getAccountId());
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.println("Balance: " + currencyFormatter.format(account.getBalance()));
            System.out.println("Status: " + account.getStatus());
            System.out.println("Created: " + account.getCreatedAt());
            System.out.println("=".repeat(45));

        } catch (Exception e) {
            System.out.println("\n❌ Failed to fetch account details: " + e.getMessage());
            logger.error("Error fetching account details", e);
        }
    }

    /**
     * Handle closing an account
     */
    private static void handleCloseAccount() {
        System.out.print("\nEnter Account Number to close: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            System.out.print("Are you sure you want to close this account? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                accountService.closeAccount(account.getAccountId());
                System.out.println("\n✅ Account closed successfully!");
            } else {
                System.out.println("\n❌ Account closure cancelled.");
            }

        } catch (Exception e) {
            System.out.println("\n❌ Failed to close account: " + e.getMessage());
            logger.error("Error closing account", e);
        }
    }

    /**
     * Show banking operations menu
     */
    private static void showBankingOperationsMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("BANKING OPERATIONS");
        System.out.println("=".repeat(45));
        System.out.println("1. Deposit Money");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Transfer Funds");
        System.out.println("4. Check Balance");
        System.out.println("5. Back to Main Menu");
        System.out.println("=".repeat(45));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleDeposit();
                    break;
                case 2:
                    handleWithdraw();
                    break;
                case 3:
                    handleTransfer();
                    break;
                case 4:
                    handleCheckBalance();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        }
    }

    /**
     * Handle deposit
     */
    private static void handleDeposit() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("DEPOSIT MONEY");
        System.out.println("=".repeat(45));

        try {
            System.out.print("Enter Account Number: ");
            String accountNumber = scanner.nextLine().trim();

            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            System.out.print("Enter Amount to Deposit: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

            Transaction transaction = transactionService.deposit(account.getAccountId(), amount);

            System.out.println("\n✅ Deposit successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount Deposited: " + currencyFormatter.format(amount));
            System.out.println("New Balance: " + currencyFormatter.format(transaction.getBalanceAfter()));

        } catch (Exception e) {
            System.out.println("\n❌ Deposit failed: " + e.getMessage());
            logger.error("Error processing deposit", e);
        }
    }

    /**
     * Handle withdraw
     */
    private static void handleWithdraw() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("WITHDRAW MONEY");
        System.out.println("=".repeat(45));

        try {
            System.out.print("Enter Account Number: ");
            String accountNumber = scanner.nextLine().trim();

            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            System.out.print("Enter Amount to Withdraw: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

            Transaction transaction = transactionService.withdraw(account.getAccountId(), amount);

            System.out.println("\n✅ Withdrawal successful!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Amount Withdrawn: " + currencyFormatter.format(amount));
            System.out.println("New Balance: " + currencyFormatter.format(transaction.getBalanceAfter()));

        } catch (Exception e) {
            System.out.println("\n❌ Withdrawal failed: " + e.getMessage());
            logger.error("Error processing withdrawal", e);
        }
    }

    /**
     * Handle fund transfer
     */
    private static void handleTransfer() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("TRANSFER FUNDS");
        System.out.println("=".repeat(45));

        try {
            System.out.print("Enter Source Account Number: ");
            String fromAccountNumber = scanner.nextLine().trim();

            Account fromAccount = accountService.getAccountByNumber(fromAccountNumber);

            // Verify ownership
            if (!fromAccount.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            System.out.print("Enter Destination Account Number: ");
            String toAccountNumber = scanner.nextLine().trim();

            Account toAccount = accountService.getAccountByNumber(toAccountNumber);

            System.out.print("Enter Amount to Transfer: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());

            Transaction[] transactions = transactionService.transfer(fromAccount.getAccountId(), 
                                                                     toAccount.getAccountId(), amount);

            System.out.println("\n✅ Transfer successful!");
            System.out.println("Amount Transferred: " + currencyFormatter.format(amount));
            System.out.println("From: " + fromAccountNumber);
            System.out.println("To: " + toAccountNumber);
            System.out.println("New Balance: " + currencyFormatter.format(transactions[0].getBalanceAfter()));

        } catch (Exception e) {
            System.out.println("\n❌ Transfer failed: " + e.getMessage());
            logger.error("Error processing transfer", e);
        }
    }

    /**
     * Handle check balance
     */
    private static void handleCheckBalance() {
        System.out.print("\nEnter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            BigDecimal balance = transactionService.checkBalance(account.getAccountId());
            System.out.println("\n💰 Current Balance: " + currencyFormatter.format(balance));

        } catch (Exception e) {
            System.out.println("\n❌ Failed to check balance: " + e.getMessage());
            logger.error("Error checking balance", e);
        }
    }

    /**
     * Show transaction history
     */
    private static void showTransactionHistory() {
        System.out.print("\nEnter Account Number: ");
        String accountNumber = scanner.nextLine().trim();

        try {
            Account account = accountService.getAccountByNumber(accountNumber);

            // Verify ownership
            if (!account.getCustomerId().equals(loggedInCustomer.getCustomerId())) {
                System.out.println("\n❌ You don't have access to this account.");
                return;
            }

            List<Transaction> transactions = transactionService.getTransactionHistory(account.getAccountId());

            if (transactions.isEmpty()) {
                System.out.println("\n📭 No transactions found for this account.");
                return;
            }

            System.out.println("\n" + "=".repeat(100));
            System.out.println("TRANSACTION HISTORY - " + accountNumber);
            System.out.println("=".repeat(100));
            System.out.printf("%-12s %-15s %-15s %-15s %-25s%n", 
                            "Trans ID", "Type", "Amount", "Balance", "Date");
            System.out.println("-".repeat(100));

            for (Transaction transaction : transactions) {
                System.out.printf("%-12d %-15s %-15s %-15s %-25s%n",
                        transaction.getTransactionId(),
                        transaction.getTransactionType(),
                        currencyFormatter.format(transaction.getAmount()),
                        currencyFormatter.format(transaction.getBalanceAfter()),
                        transaction.getTransactionDate());
            }
            System.out.println("=".repeat(100));

        } catch (Exception e) {
            System.out.println("\n❌ Failed to fetch transaction history: " + e.getMessage());
            logger.error("Error fetching transaction history", e);
        }
    }

    /**
     * Show profile management
     */
    private static void showProfileManagement() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("PROFILE MANAGEMENT");
        System.out.println("=".repeat(45));
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Back to Main Menu");
        System.out.println("=".repeat(45));
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    handleViewProfile();
                    break;
                case 2:
                    handleChangePassword();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a number.");
        }
    }

    /**
     * Handle view profile
     */
    private static void handleViewProfile() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("YOUR PROFILE");
        System.out.println("=".repeat(45));
        System.out.println("Customer ID: " + loggedInCustomer.getCustomerId());
        System.out.println("Name: " + loggedInCustomer.getFullName());
        System.out.println("Email: " + loggedInCustomer.getEmail());
        System.out.println("Phone: " + loggedInCustomer.getPhone());
        System.out.println("Address: " + loggedInCustomer.getAddress());
        System.out.println("Username: " + loggedInCustomer.getUsername());
        System.out.println("Member Since: " + loggedInCustomer.getCreatedAt());
        System.out.println("=".repeat(45));
    }

    /**
     * Handle change password
     */
    private static void handleChangePassword() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("CHANGE PASSWORD");
        System.out.println("=".repeat(45));

        try {
            System.out.print("Current Password: ");
            String currentPassword = scanner.nextLine().trim();

            System.out.print("New Password: ");
            String newPassword = scanner.nextLine().trim();

            System.out.print("Confirm New Password: ");
            String confirmPassword = scanner.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("\n❌ Passwords do not match!");
                return;
            }

            authService.changePassword(loggedInCustomer.getCustomerId(), currentPassword, newPassword);
            System.out.println("\n✅ Password changed successfully!");

        } catch (Exception e) {
            System.out.println("\n❌ Failed to change password: " + e.getMessage());
            logger.error("Error changing password", e);
        }
    }

    /**
     * Handle logout
     */
    private static void handleLogout() {
        logger.info("User logged out: {}", loggedInCustomer.getUsername());
        loggedInCustomer = null;
        System.out.println("\n✅ Logged out successfully!");
    }
}
