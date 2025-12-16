-- Banking Management System Database Schema
-- MySQL Database Setup Script

-- Drop database if exists (use with caution in production)
-- DROP DATABASE IF EXISTS banking_db;

-- Create database
CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

-- Table: customers
CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(10) NOT NULL,
    address TEXT NOT NULL,
    username VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: accounts
CREATE TABLE IF NOT EXISTS accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    account_number VARCHAR(12) NOT NULL UNIQUE,
    account_type ENUM('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT', 'RECURRING_DEPOSIT') NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_account_number (account_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    related_account_id BIGINT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_transaction_type (transaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample admin customer (password: Admin@123)
-- Password hash generated using BCrypt with work factor 12
INSERT INTO customers (first_name, last_name, email, phone, address, username, password_hash)
VALUES ('Admin', 'User', 'admin@bankingsystem.com', '9999999999', '123 Admin Street', 'admin',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIEe9ZCXCS');

-- Insert sample customer (password: Test@123)
INSERT INTO customers (first_name, last_name, email, phone, address, username, password_hash)
VALUES ('John', 'Doe', 'john.doe@example.com', '9876543210', '456 Main Street', 'johndoe',
        '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

-- Insert sample accounts for John Doe (assuming customer_id = 2)
INSERT INTO accounts (customer_id, account_number, account_type, balance, status)
VALUES 
(2, 'ACC123456789', 'SAVINGS', 10000.00, 'ACTIVE'),
(2, 'ACC987654321', 'CURRENT', 5000.00, 'ACTIVE');

-- Insert sample transactions
INSERT INTO transactions (account_id, transaction_type, amount, balance_after, description)
VALUES 
(1, 'DEPOSIT', 10000.00, 10000.00, 'Initial deposit'),
(2, 'DEPOSIT', 5000.00, 5000.00, 'Initial deposit');

-- View to get customer account summary
CREATE OR REPLACE VIEW customer_account_summary AS
SELECT 
    c.customer_id,
    c.first_name,
    c.last_name,
    c.username,
    c.email,
    COUNT(a.account_id) AS total_accounts,
    SUM(a.balance) AS total_balance
FROM customers c
LEFT JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
GROUP BY c.customer_id, c.first_name, c.last_name, c.username, c.email;

-- View to get account transaction summary
CREATE OR REPLACE VIEW account_transaction_summary AS
SELECT 
    a.account_id,
    a.account_number,
    a.account_type,
    a.balance,
    COUNT(t.transaction_id) AS total_transactions,
    SUM(CASE WHEN t.transaction_type IN ('DEPOSIT', 'TRANSFER_IN') THEN t.amount ELSE 0 END) AS total_credits,
    SUM(CASE WHEN t.transaction_type IN ('WITHDRAWAL', 'TRANSFER_OUT') THEN t.amount ELSE 0 END) AS total_debits
FROM accounts a
LEFT JOIN transactions t ON a.account_id = t.account_id
GROUP BY a.account_id, a.account_number, a.account_type, a.balance;

-- Show tables
SHOW TABLES;

-- Display sample data
SELECT 'Customers' AS 'Table';
SELECT * FROM customers;

SELECT 'Accounts' AS 'Table';
SELECT * FROM accounts;

SELECT 'Transactions' AS 'Table';
SELECT * FROM transactions;

SELECT 'Customer Account Summary' AS 'View';
SELECT * FROM customer_account_summary;

SELECT 'Account Transaction Summary' AS 'View';
SELECT * FROM account_transaction_summary;
