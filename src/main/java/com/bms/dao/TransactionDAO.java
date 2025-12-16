package com.bms.dao;

import com.bms.model.Transaction;
import com.bms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transaction entity
 * Handles all database operations for transactions
 */
public class TransactionDAO {
    private static final Logger logger = LoggerFactory.getLogger(TransactionDAO.class);
    private final DatabaseConnection dbConnection;

    public TransactionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new transaction
     * @param transaction Transaction object to create
     * @return Generated transaction ID
     * @throws SQLException if database operation fails
     */
    public Long create(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, balance_after, " +
                     "description, related_account_id, transaction_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, transaction.getAccountId());
            stmt.setString(2, transaction.getTransactionType().name());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setBigDecimal(4, transaction.getBalanceAfter());
            stmt.setString(5, transaction.getDescription());
            
            if (transaction.getRelatedAccountId() != null) {
                stmt.setLong(6, transaction.getRelatedAccountId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            stmt.setTimestamp(7, Timestamp.valueOf(transaction.getTransactionDate()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating transaction failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long transactionId = generatedKeys.getLong(1);
                    transaction.setTransactionId(transactionId);
                    logger.info("Transaction created successfully with ID: {}", transactionId);
                    return transactionId;
                } else {
                    throw new SQLException("Creating transaction failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating transaction", e);
            throw e;
        }
    }

    /**
     * Find transaction by ID
     * @param transactionId Transaction ID
     * @return Optional containing transaction if found
     * @throws SQLException if database operation fails
     */
    public Optional<Transaction> findById(Long transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transaction transaction = extractTransactionFromResultSet(rs);
                    logger.debug("Transaction found with ID: {}", transactionId);
                    return Optional.of(transaction);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding transaction by ID: {}", transactionId, e);
            throw e;
        }
        
        logger.debug("Transaction not found with ID: {}", transactionId);
        return Optional.empty();
    }

    /**
     * Find all transactions for an account
     * @param accountId Account ID
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByAccountId(Long accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
            
            logger.debug("Retrieved {} transactions for account: {}", transactions.size(), accountId);
        } catch (SQLException e) {
            logger.error("Error finding transactions for account: {}", accountId, e);
            throw e;
        }
        
        return transactions;
    }

    /**
     * Find transactions for an account within a date range
     * @param accountId Account ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, 
                                                         LocalDateTime endDate) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND transaction_date BETWEEN ? AND ? " +
                     "ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setTimestamp(2, Timestamp.valueOf(startDate));
            stmt.setTimestamp(3, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
            
            logger.debug("Retrieved {} transactions for account: {} in date range", 
                        transactions.size(), accountId);
        } catch (SQLException e) {
            logger.error("Error finding transactions for account: {} in date range", accountId, e);
            throw e;
        }
        
        return transactions;
    }

    /**
     * Find last N transactions for an account
     * @param accountId Account ID
     * @param limit Number of transactions to retrieve
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findLastNTransactions(Long accountId, int limit) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC LIMIT ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
            
            logger.debug("Retrieved {} recent transactions for account: {}", transactions.size(), accountId);
        } catch (SQLException e) {
            logger.error("Error finding recent transactions for account: {}", accountId, e);
            throw e;
        }
        
        return transactions;
    }

    /**
     * Find all transactions by type
     * @param accountId Account ID
     * @param transactionType Transaction type
     * @return List of transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findByAccountIdAndType(Long accountId, 
                                                    Transaction.TransactionType transactionType) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? AND transaction_type = ? " +
                     "ORDER BY transaction_date DESC";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            stmt.setString(2, transactionType.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(extractTransactionFromResultSet(rs));
                }
            }
            
            logger.debug("Retrieved {} transactions of type {} for account: {}", 
                        transactions.size(), transactionType, accountId);
        } catch (SQLException e) {
            logger.error("Error finding transactions by type for account: {}", accountId, e);
            throw e;
        }
        
        return transactions;
    }

    /**
     * Get all transactions (Admin function)
     * @return List of all transactions
     * @throws SQLException if database operation fails
     */
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT 1000";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
            logger.debug("Retrieved {} transactions", transactions.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all transactions", e);
            throw e;
        }
        
        return transactions;
    }

    /**
     * Get transaction count for an account
     * @param accountId Account ID
     * @return Transaction count
     * @throws SQLException if database operation fails
     */
    public int getTransactionCount(Long accountId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting transaction count for account: {}", accountId, e);
            throw e;
        }
        
        return 0;
    }

    /**
     * Delete all transactions for an account
     * @param accountId Account ID
     * @return Number of transactions deleted
     * @throws SQLException if database operation fails
     */
    public int deleteByAccountId(Long accountId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            int deletedCount = stmt.executeUpdate();
            logger.info("Deleted {} transactions for account: {}", deletedCount, accountId);
            return deletedCount;
        } catch (SQLException e) {
            logger.error("Error deleting transactions for account: {}", accountId, e);
            throw e;
        }
    }

    /**
     * Extract Transaction object from ResultSet
     * @param rs ResultSet
     * @return Transaction object
     * @throws SQLException if extraction fails
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getLong("transaction_id"));
        transaction.setAccountId(rs.getLong("account_id"));
        transaction.setTransactionType(Transaction.TransactionType.valueOf(rs.getString("transaction_type")));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setBalanceAfter(rs.getBigDecimal("balance_after"));
        transaction.setDescription(rs.getString("description"));
        
        Long relatedAccountId = rs.getLong("related_account_id");
        if (!rs.wasNull()) {
            transaction.setRelatedAccountId(relatedAccountId);
        }
        
        transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
        return transaction;
    }
}
