package com.bms.dao;

import com.bms.model.Customer;
import com.bms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Customer entity
 * Handles all database operations for customers
 */
public class CustomerDAO {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDAO.class);
    private final DatabaseConnection dbConnection;

    public CustomerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Create a new customer
     * @param customer Customer object to create
     * @return Generated customer ID
     * @throws SQLException if database operation fails
     */
    public Long create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, username, password_hash) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getUsername());
            stmt.setString(7, customer.getPasswordHash());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long customerId = generatedKeys.getLong(1);
                    customer.setCustomerId(customerId);
                    logger.info("Customer created successfully with ID: {}", customerId);
                    return customerId;
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating customer", e);
            throw e;
        }
    }

    /**
     * Find customer by ID
     * @param customerId Customer ID
     * @return Optional containing customer if found
     * @throws SQLException if database operation fails
     */
    public Optional<Customer> findById(Long customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = extractCustomerFromResultSet(rs);
                    logger.debug("Customer found with ID: {}", customerId);
                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by ID: {}", customerId, e);
            throw e;
        }
        
        logger.debug("Customer not found with ID: {}", customerId);
        return Optional.empty();
    }

    /**
     * Find customer by username
     * @param username Username
     * @return Optional containing customer if found
     * @throws SQLException if database operation fails
     */
    public Optional<Customer> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = extractCustomerFromResultSet(rs);
                    logger.debug("Customer found with username: {}", username);
                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by username: {}", username, e);
            throw e;
        }
        
        return Optional.empty();
    }

    /**
     * Find customer by email
     * @param email Email address
     * @return Optional containing customer if found
     * @throws SQLException if database operation fails
     */
    public Optional<Customer> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractCustomerFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding customer by email: {}", email, e);
            throw e;
        }
        
        return Optional.empty();
    }

    /**
     * Update customer information
     * @param customer Customer object with updated information
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, " +
                     "phone = ?, address = ?, updated_at = CURRENT_TIMESTAMP WHERE customer_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setLong(6, customer.getCustomerId());
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Customer updated successfully: {}", customer.getCustomerId());
            } else {
                logger.warn("No customer found to update with ID: {}", customer.getCustomerId());
            }
            
            return success;
        } catch (SQLException e) {
            logger.error("Error updating customer: {}", customer.getCustomerId(), e);
            throw e;
        }
    }

    /**
     * Update customer password
     * @param customerId Customer ID
     * @param newPasswordHash New hashed password
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updatePassword(Long customerId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE customers SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE customer_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPasswordHash);
            stmt.setLong(2, customerId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Password updated for customer: {}", customerId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error updating password for customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Delete customer
     * @param customerId Customer ID
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    public boolean delete(Long customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;
            
            if (success) {
                logger.info("Customer deleted successfully: {}", customerId);
            } else {
                logger.warn("No customer found to delete with ID: {}", customerId);
            }
            
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting customer: {}", customerId, e);
            throw e;
        }
    }

    /**
     * Get all customers (Admin function)
     * @return List of all customers
     * @throws SQLException if database operation fails
     */
    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
            
            logger.debug("Retrieved {} customers", customers.size());
        } catch (SQLException e) {
            logger.error("Error retrieving all customers", e);
            throw e;
        }
        
        return customers;
    }

    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     * @throws SQLException if database operation fails
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE username = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking username existence: {}", username, e);
            throw e;
        }
        
        return false;
    }

    /**
     * Check if email exists
     * @param email Email to check
     * @return true if email exists
     * @throws SQLException if database operation fails
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence: {}", email, e);
            throw e;
        }
        
        return false;
    }

    /**
     * Extract Customer object from ResultSet
     * @param rs ResultSet
     * @return Customer object
     * @throws SQLException if extraction fails
     */
    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getLong("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setUsername(rs.getString("username"));
        customer.setPasswordHash(rs.getString("password_hash"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return customer;
    }
}
