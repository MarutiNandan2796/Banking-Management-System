package com.bms.dao;

import com.bms.model.Account;
import com.bms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Account entity
 * Handles all database operations for accounts
 */
public class AccountDAO {
    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
    private final DatabaseConnection dbConnection;

    public AccountDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new account
     * @param account Account object to create
     * @return Generated account ID
     * @throws SQLException if database operation fails
     */
    public Long create(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, balance, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setString(3, account.getAccountType().name());
            stmt.setBigDecimal(4, account.getBalance());
            stmt.setString(5, account.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long accountId = generatedKeys.getLong(1);
                    account.setAccountId(accountId);
                    logger.info("Account created successfully with ID: {}", accountId);
                    return accountId;
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating account", e);
            throw e;
        }
    }

    /**
     * Find account by ID
     * @param accountId Account ID
     * @return Optional containing account if found
     * @throws SQLException if database operation fails
     */
    public Optional<Account> findById(Long accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = extractAccountFromResultSet(rs);
                    logger.debug("Account found with ID: {}", accountId);
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding account by ID: {}", accountId, e);
            throw e;
        }
        
        logger.debug("Account not found with ID: {}", accountId);
        return Optional.empty();
    }

    /**
     * Find account by account number
     * @param accountNumber Account number
     * @return Optional containing account if found
     * @throws SQLException if database operation fails
     */
    public Optional<Account> findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = extractAccountFromResultSet(rs);
                    logger.debug("Account found with number: {}", accountNumber);
                    return Optional.of(account);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding account by number: {}", accountNumber, e);
            throw e;
        }
        
        return Optional.empty();
    }

    /**
     * Find all accounts for a customer
     * @param customerId Customer ID
     * @return List of accounts
     * @throws SQLException if database operation fails
     */
    public List<Account> findByCustomerId(Long customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(extractAccountFromResultSet(rs));
                }
            }
            
            logger.debug("Retrieved {} accounts for customer: {}", accounts.size(), customerId);
        } catch (SQLException e) {
            logger.error("Error finding accounts for customer: {}", customerId, e);
            throw e;
        }
        
        return accounts;
    }

    /**
     * Update account information
     * @param account Account object with updated information
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean update(Account account) throws SQLException {
        String sql = "UPDATE accounts SET account_type = ?, balance = ?, status = ?, " +
                     "updated_at = CURRENT_TIMESTAMP WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, account.getAccountType().name());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getStatus().name());
            stmt.setLong(4, account.getAccountId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Account updated successfully: {}", account.getAccountId());
            } else {
                logger.warn("No account found to update with ID: {}", account.getAccountId());
            }
            
            return success;
        } catch (SQLException e) {
            logger.error("Error updating account: {}", account.getAccountId(), e);
            throw e;
        }
    }

    /**
     * Update account balance
     * @param accountId Account ID
     * @param newBalance New balance
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateBalance(Long accountId, java.math.BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ?, updated_at = CURRENT_TIMESTAMP WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, newBalance);
            stmt.setLong(2, accountId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Balance updated for account: {}", accountId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error updating balance for account: {}", accountId, e);
            throw e;
        }
    }

    /**
     * Update account status
     * @param accountId Account ID
     * @param status New status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateStatus(Long accountId, Account.AccountStatus status) throws SQLException {
        String sql = "UPDATE accounts SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setLong(2, accountId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Status updated for account: {}", accountId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error updating status for account: {}", accountId, e);
            throw e;
        }
    }

    /**
     * Delete account
     * @param accountId Account ID
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean delete(Long accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, accountId);
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Account deleted successfully: {}", accountId);
            } else {
                logger.warn("No account found to delete with ID: {}", accountId);
            }
            
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting account: {}", accountId, e);
            throw e;
        }
    }

    /**
     * Get all accounts (Admin function)
     * @return List of all accounts
     * @throws SQLException if database operation fails
     */
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
            
            logger.debug("Retrieved {} accounts", accounts.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all accounts", e);
            throw e;
        }
        
        return accounts;
    }

    /**
     * Check if account number exists
     * @param accountNumber Account number to check
     * @return true if account number exists
     * @throws SQLException if database operation fails
     */
    public boolean accountNumberExists(String accountNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking account number existence: {}", accountNumber, e);
            throw e;
        }
        
        return false;
    }

    /**
     * Extract Account object from ResultSet
     * @param rs ResultSet
     * @return Account object
     * @throws SQLException if extraction fails
     */
    private Account extractAccountFromResultSet(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setCustomerId(rs.getLong("customer_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(Account.AccountType.valueOf(rs.getString("account_type")));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(Account.AccountStatus.valueOf(rs.getString("status")));
        account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return account;
    }
}
