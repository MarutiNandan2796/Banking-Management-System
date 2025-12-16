package com.bms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for managing database connections
 * Uses Singleton pattern to ensure single database configuration
 */
public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    private static DatabaseConnection instance;
    private String url;
    private String username;
    private String password;

    private DatabaseConnection() {
        loadProperties();
    }

    /**
     * Get singleton instance of DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Load database properties from db.properties file
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                logger.error("Unable to find db.properties");
                throw new RuntimeException("db.properties file not found");
            }
            props.load(input);
            
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("Database configuration loaded successfully");
            
        } catch (IOException e) {
            logger.error("Error loading database properties", e);
            throw new RuntimeException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Get a new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            logger.debug("Database connection established");
            return conn;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection", e);
            throw e;
        }
    }

    /**
     * Close database connection
     * @param connection Connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                logger.debug("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Test database connectivity
     * @return true if connection successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            logger.info("Database connection test successful");
            return true;
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
}
