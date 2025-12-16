package com.bms.service;

import com.bms.dao.AccountDAO;
import com.bms.dao.TransactionDAO;
import com.bms.model.Account;
import com.bms.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for transaction operations
 */
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public TransactionService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Deposit money into an account
     * @param accountId Account ID
     * @param amount Amount to deposit
     * @return Transaction object
     * @throws Exception if deposit fails
     */
    public Transaction deposit(Long accountId, BigDecimal amount) throws Exception {
        logger.info("Processing deposit of {} for account ID: {}", amount, accountId);

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        try {
            // Get account
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = accountOpt.get();

            // Check account status
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("Account is not active");
            }

            // Update balance
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);

            // Update account
            accountDAO.update(account);

            // Create transaction record
            Transaction transaction = new Transaction(
                    accountId,
                    Transaction.TransactionType.DEPOSIT,
                    amount,
                    newBalance,
                    "Deposit"
            );
            transactionDAO.create(transaction);

            logger.info("Deposit successful for account ID: {}", accountId);
            return transaction;

        } catch (SQLException e) {
            logger.error("Error processing deposit", e);
            throw new Exception("Failed to process deposit: " + e.getMessage());
        }
    }

    /**
     * Withdraw money from an account
     * @param accountId Account ID
     * @param amount Amount to withdraw
     * @return Transaction object
     * @throws Exception if withdrawal fails
     */
    public Transaction withdraw(Long accountId, BigDecimal amount) throws Exception {
        logger.info("Processing withdrawal of {} for account ID: {}", amount, accountId);

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        try {
            // Get account
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = accountOpt.get();

            // Check account status
            if (account.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("Account is not active");
            }

            // Check sufficient balance
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }

            // Update balance
            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);

            // Update account
            accountDAO.update(account);

            // Create transaction record
            Transaction transaction = new Transaction(
                    accountId,
                    Transaction.TransactionType.WITHDRAWAL,
                    amount,
                    newBalance,
                    "Withdrawal"
            );
            transactionDAO.create(transaction);

            logger.info("Withdrawal successful for account ID: {}", accountId);
            return transaction;

        } catch (SQLException e) {
            logger.error("Error processing withdrawal", e);
            throw new Exception("Failed to process withdrawal: " + e.getMessage());
        }
    }

    /**
     * Transfer money between accounts
     * @param fromAccountId Source account ID
     * @param toAccountId Destination account ID
     * @param amount Amount to transfer
     * @return Array of two transactions [from, to]
     * @throws Exception if transfer fails
     */
    public Transaction[] transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) throws Exception {
        logger.info("Processing transfer of {} from account {} to account {}", 
                   amount, fromAccountId, toAccountId);

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        try {
            // Get source account
            Optional<Account> fromAccountOpt = accountDAO.findById(fromAccountId);
            if (fromAccountOpt.isEmpty()) {
                throw new IllegalArgumentException("Source account not found");
            }
            Account fromAccount = fromAccountOpt.get();

            // Get destination account
            Optional<Account> toAccountOpt = accountDAO.findById(toAccountId);
            if (toAccountOpt.isEmpty()) {
                throw new IllegalArgumentException("Destination account not found");
            }
            Account toAccount = toAccountOpt.get();

            // Check account statuses
            if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("Source account is not active");
            }
            if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
                throw new IllegalArgumentException("Destination account is not active");
            }

            // Check sufficient balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance in source account");
            }

            // Deduct from source account
            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            fromAccount.setBalance(newFromBalance);
            accountDAO.update(fromAccount);

            // Add to destination account
            BigDecimal newToBalance = toAccount.getBalance().add(amount);
            toAccount.setBalance(newToBalance);
            accountDAO.update(toAccount);

            // Create transaction records
            Transaction fromTransaction = new Transaction(
                    fromAccountId,
                    Transaction.TransactionType.TRANSFER_OUT,
                    amount,
                    newFromBalance,
                    "Transfer to account " + toAccount.getAccountNumber(),
                    toAccountId
            );
            transactionDAO.create(fromTransaction);

            Transaction toTransaction = new Transaction(
                    toAccountId,
                    Transaction.TransactionType.TRANSFER_IN,
                    amount,
                    newToBalance,
                    "Transfer from account " + fromAccount.getAccountNumber(),
                    fromAccountId
            );
            transactionDAO.create(toTransaction);

            logger.info("Transfer successful from account {} to account {}", fromAccountId, toAccountId);
            return new Transaction[]{fromTransaction, toTransaction};

        } catch (SQLException e) {
            logger.error("Error processing transfer", e);
            throw new Exception("Failed to process transfer: " + e.getMessage());
        }
    }

    /**
     * Check account balance
     * @param accountId Account ID
     * @return Current balance
     * @throws Exception if operation fails
     */
    public BigDecimal checkBalance(Long accountId) throws Exception {
        try {
            Optional<Account> accountOpt = accountDAO.findById(accountId);
            if (accountOpt.isEmpty()) {
                throw new IllegalArgumentException("Account not found");
            }
            return accountOpt.get().getBalance();
        } catch (SQLException e) {
            logger.error("Error checking balance", e);
            throw new Exception("Failed to check balance: " + e.getMessage());
        }
    }

    /**
     * Get transaction history for an account
     * @param accountId Account ID
     * @return List of transactions
     * @throws Exception if operation fails
     */
    public List<Transaction> getTransactionHistory(Long accountId) throws Exception {
        try {
            return transactionDAO.findByAccountId(accountId);
        } catch (SQLException e) {
            logger.error("Error fetching transaction history", e);
            throw new Exception("Failed to fetch transaction history: " + e.getMessage());
        }
    }

    /**
     * Get transaction history for an account within a date range
     * @param accountId Account ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     * @throws Exception if operation fails
     */
    public List<Transaction> getTransactionHistory(Long accountId, LocalDateTime startDate, 
                                                   LocalDateTime endDate) throws Exception {
        try {
            return transactionDAO.findByAccountIdAndDateRange(accountId, startDate, endDate);
        } catch (SQLException e) {
            logger.error("Error fetching transaction history", e);
            throw new Exception("Failed to fetch transaction history: " + e.getMessage());
        }
    }

    /**
     * Get last N transactions for an account
     * @param accountId Account ID
     * @param limit Number of transactions
     * @return List of transactions
     * @throws Exception if operation fails
     */
    public List<Transaction> getRecentTransactions(Long accountId, int limit) throws Exception {
        try {
            return transactionDAO.findLastNTransactions(accountId, limit);
        } catch (SQLException e) {
            logger.error("Error fetching recent transactions", e);
            throw new Exception("Failed to fetch recent transactions: " + e.getMessage());
        }
    }

    /**
     * Get all transactions (Admin function)
     * @return List of all transactions
     * @throws Exception if operation fails
     */
    public List<Transaction> getAllTransactions() throws Exception {
        try {
            return transactionDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error fetching all transactions", e);
            throw new Exception("Failed to fetch all transactions: " + e.getMessage());
        }
    }

    /**
     * Get transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction object
     * @throws Exception if operation fails
     */
    public Transaction getTransactionById(Long transactionId) throws Exception {
        try {
            Optional<Transaction> transactionOpt = transactionDAO.findById(transactionId);
            if (transactionOpt.isEmpty()) {
                throw new IllegalArgumentException("Transaction not found");
            }
            return transactionOpt.get();
        } catch (SQLException e) {
            logger.error("Error fetching transaction", e);
            throw new Exception("Failed to fetch transaction: " + e.getMessage());
        }
    }
}
