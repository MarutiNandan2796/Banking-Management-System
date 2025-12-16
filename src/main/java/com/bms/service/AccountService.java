package com.bms.service;

import com.bms.dao.AccountDAO;
import com.bms.dao.TransactionDAO;
import com.bms.model.Account;
import com.bms.model.Transaction;
import com.bms.util.AccountNumberGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for account operations
 */
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Open a new account for a customer
     * @param customerId Customer ID
     * @param accountType Account type
     * @param initialDeposit Initial deposit amount (can be zero)
     * @return Created account
     * @throws Exception if account creation fails
     */
    public Account openAccount(Long customerId, Account.AccountType accountType, 
                              BigDecimal initialDeposit) throws Exception {
        logger.info("Opening new {} account for customer ID: {}", accountType, customerId);

        // Validate initial deposit
        if (initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative");
        }

        // Generate unique account number
        String accountNumber = generateUniqueAccountNumber();

        // Create account
        Account account = new Account(customerId, accountNumber, accountType, initialDeposit);

        try {
            Long accountId = accountDAO.create(account);
            account.setAccountId(accountId);

            // Create initial deposit transaction if amount > 0
            if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
                Transaction transaction = new Transaction(
                        accountId,
                        Transaction.TransactionType.DEPOSIT,
                        initialDeposit,
                        initialDeposit,
                        "Initial deposit"
                );
                transactionDAO.create(transaction);
            }

            logger.info("Account opened successfully: {}", accountNumber);
            return account;

        } catch (SQLException e) {
            logger.error("Error opening account", e);
            throw new Exception("Failed to open account: " + e.getMessage());
        }
    }

    /**
     * Get account details by account ID
     * @param accountId Account ID
     * @return Account object
     * @throws Exception if account not found
     */
    public Account getAccountById(Long accountId) throws Exception {
        try {
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }
            return accountOpt.get();
        } catch (SQLException e) {
            logger.error("Error fetching account", e);
            throw new Exception("Failed to fetch account: " + e.getMessage());
        }
    }

    /**
     * Get account details by account number
     * @param accountNumber Account number
     * @return Account object
     * @throws Exception if account not found
     */
    public Account getAccountByNumber(String accountNumber) throws Exception {
        try {
            Optional<Account> accountOpt = accountDAO.findByAccountNumber(accountNumber);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }
            return accountOpt.get();
        } catch (SQLException e) {
            logger.error("Error fetching account", e);
            throw new Exception("Failed to fetch account: " + e.getMessage());
        }
    }

    /**
     * Get all accounts for a customer
     * @param customerId Customer ID
     * @return List of accounts
     * @throws Exception if operation fails
     */
    public List<Account> getAccountsByCustomerId(Long customerId) throws Exception {
        try {
            return accountDAO.findByCustomerId(customerId);
        } catch (SQLException e) {
            logger.error("Error fetching customer accounts", e);
            throw new Exception("Failed to fetch customer accounts: " + e.getMessage());
        }
    }

    /**
     * Update account type
     * @param accountId Account ID
     * @param newAccountType New account type
     * @throws Exception if update fails
     */
    public void updateAccountType(Long accountId, Account.AccountType newAccountType) throws Exception {
        logger.info("Updating account type for account ID: {}", accountId);

        try {
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = accountOpt.get();
            account.setAccountType(newAccountType);
            
            boolean success = accountDAO.update(account);
            if (!success) {
                throw new Exception("Failed to update account type");
            }

            logger.info("Account type updated successfully for account ID: {}", accountId);

        } catch (SQLException e) {
            logger.error("Error updating account type", e);
            throw new Exception("Failed to update account type: " + e.getMessage());
        }
    }

    /**
     * Close an account
     * @param accountId Account ID
     * @throws Exception if account closure fails
     */
    public void closeAccount(Long accountId) throws Exception {
        logger.info("Closing account ID: {}", accountId);

        try {
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = accountOpt.get();

            // Check if account has balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException("Cannot close account with non-zero balance. " +
                        "Please withdraw all funds first.");
            }

            // Update account status to CLOSED
            account.setStatus(Account.AccountStatus.CLOSED);
            boolean success = accountDAO.update(account);

            if (!success) {
                throw new Exception("Failed to close account");
            }

            logger.info("Account closed successfully: {}", accountId);

        } catch (SQLException e) {
            logger.error("Error closing account", e);
            throw new Exception("Failed to close account: " + e.getMessage());
        }
    }

    /**
     * Delete an account (hard delete - admin function)
     * @param accountId Account ID
     * @throws Exception if deletion fails
     */
    public void deleteAccount(Long accountId) throws Exception {
        logger.info("Deleting account ID: {}", accountId);

        try {
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = accountOpt.get();

            // Check if account has balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException("Cannot delete account with non-zero balance");
            }

            // Delete all transactions first
            transactionDAO.deleteByAccountId(accountId);

            // Delete account
            boolean success = accountDAO.delete(accountId);
            if (!success) {
                throw new Exception("Failed to delete account");
            }

            logger.info("Account deleted successfully: {}", accountId);

        } catch (SQLException e) {
            logger.error("Error deleting account", e);
            throw new Exception("Failed to delete account: " + e.getMessage());
        }
    }

    /**
     * Get all accounts (Admin function)
     * @return List of all accounts
     * @throws Exception if operation fails
     */
    public List<Account> getAllAccounts() throws Exception {
        try {
            return accountDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching all accounts", e);
            throw new Exception("Failed to fetch all accounts: " + e.getMessage());
        }
    }

    /**
     * Generate a unique account number
     * @return Unique account number
     * @throws SQLException if database operation fails
     */
    private String generateUniqueAccountNumber() throws SQLException {
        String accountNumber;
        do {
            accountNumber = AccountNumberGenerator.generateAccountNumber();
        } while (accountDAO.accountNumberExists(accountNumber));
        return accountNumber;
    }

    /**
     * Suspend an account
     * @param accountId Account ID
     * @throws Exception if operation fails
     */
    public void suspendAccount(Long accountId) throws Exception {
        logger.info("Suspending account ID: {}", accountId);

        try {
            boolean success = accountDAO.updateStatus(accountId, Account.AccountStatus.SUSPENDED);
            if (!success) {
                throw new Exception("Failed to suspend account");
            }
            logger.info("Account suspended successfully: {}", accountId);
        } catch (SQLException e) {
            logger.error("Error suspending account", e);
            throw new Exception("Failed to suspend account: " + e.getMessage());
        }
    }

    /**
     * Activate a suspended account
     * @param accountId Account ID
     * @throws Exception if operation fails
     */
    public void activateAccount(Long accountId) throws Exception {
        logger.info("Activating account ID: {}", accountId);

        try {
            boolean success = accountDAO.updateStatus(accountId, Account.AccountStatus.ACTIVE);
            if (!success) {
                throw new Exception("Failed to activate account");
            }
            logger.info("Account activated successfully: {}", accountId);
        } catch (SQLException e) {
            logger.error("Error activating account", e);
            throw new Exception("Failed to activate account: " + e.getMessage());
        }
    }
}
