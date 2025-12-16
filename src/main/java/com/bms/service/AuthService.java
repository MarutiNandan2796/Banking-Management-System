package com.bms.service;

import com.bms.dao.CustomerDAO;
import com.bms.model.Customer;
import com.bms.util.PasswordUtil;
import com.bms.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service class for authentication and customer registration
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final CustomerDAO customerDAO;

    public AuthService() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Register a new customer
     * @param customer Customer object with registration details
     * @return Registered customer ID
     * @throws Exception if registration fails
     */
    public Long register(Customer customer) throws Exception {
        logger.info("Attempting to register new customer: {}", customer.getUsername());

        // Validate input
        validateRegistration(customer);

        // Check if username already exists
        if (customerDAO.usernameExists(customer.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (customerDAO.emailExists(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create customer
        try {
            Long customerId = customerDAO.create(customer);
            logger.info("Customer registered successfully with ID: {}", customerId);
            return customerId;
        } catch (SQLException e) {
            logger.error("Error registering customer", e);
            throw new Exception("Failed to register customer: " + e.getMessage());
        }
    }

    /**
     * Authenticate a customer
     * @param username Username
     * @param password Plain text password
     * @return Customer object if authentication successful
     * @throws Exception if authentication fails
     */
    public Customer login(String username, String password) throws Exception {
        logger.info("Attempting login for username: {}", username);

        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Username and password are required");
        }

        try {
            Optional<Customer> customerOpt = customerDAO.findByUsername(username);

            if (customerOpt.isEmpty()) {
                logger.warn("Login failed: Username not found: {}", username);
                throw new IllegalArgumentException("Invalid username or password");
            }

            Customer customer = customerOpt.get();

            // Verify password
            if (!PasswordUtil.verifyPassword(password, customer.getPasswordHash())) {
                logger.warn("Login failed: Invalid password for username: {}", username);
                throw new IllegalArgumentException("Invalid username or password");
            }

            logger.info("Login successful for username: {}", username);
            return customer;

        } catch (SQLException e) {
            logger.error("Error during login", e);
            throw new Exception("Login failed: " + e.getMessage());
        }
    }

    /**
     * Change customer password
     * @param customerId Customer ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @throws Exception if password change fails
     */
    public void changePassword(Long customerId, String oldPassword, String newPassword) throws Exception {
        logger.info("Attempting to change password for customer ID: {}", customerId);

        try {
            // Fetch customer
            Optional<Customer> customerOpt = customerDAO.findById(customerId);
            if (customerOpt.isEmpty()) {
                throw new IllegalArgumentException("Customer not found");
            }

            Customer customer = customerOpt.get();

            // Verify old password
            if (!PasswordUtil.verifyPassword(oldPassword, customer.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Validate new password strength
            if (!PasswordUtil.isPasswordStrong(newPassword)) {
                throw new IllegalArgumentException("New password does not meet strength requirements:\n" +
                        PasswordUtil.getPasswordRequirements());
            }

            // Hash and update new password
            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            boolean success = customerDAO.updatePassword(customerId, newPasswordHash);

            if (success) {
                logger.info("Password changed successfully for customer ID: {}", customerId);
            } else {
                throw new Exception("Failed to update password");
            }

        } catch (SQLException e) {
            logger.error("Error changing password", e);
            throw new Exception("Failed to change password: " + e.getMessage());
        }
    }

    /**
     * Validate customer registration data
     * @param customer Customer object to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRegistration(Customer customer) {
        if (!ValidationUtil.isValidName(customer.getFirstName())) {
            throw new IllegalArgumentException("Invalid first name format");
        }

        if (!ValidationUtil.isValidName(customer.getLastName())) {
            throw new IllegalArgumentException("Invalid last name format");
        }

        if (!ValidationUtil.isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidPhone(customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format (should be 10 digits)");
        }

        if (!ValidationUtil.isValidUsername(customer.getUsername())) {
            throw new IllegalArgumentException("Invalid username format (3-20 alphanumeric characters)");
        }

        if (!ValidationUtil.isNotEmpty(customer.getAddress())) {
            throw new IllegalArgumentException("Address is required");
        }

        if (!ValidationUtil.isNotEmpty(customer.getPasswordHash())) {
            throw new IllegalArgumentException("Password is required");
        }
    }
}
