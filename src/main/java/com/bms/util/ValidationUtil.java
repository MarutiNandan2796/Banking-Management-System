package com.bms.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format (10 digits)
     * @param phone Phone number to validate
     * @return true if valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate username format (3-20 alphanumeric characters)
     * @param username Username to validate
     * @return true if valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validate name (letters and spaces only, 2-50 characters)
     * @param name Name to validate
     * @return true if valid
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return name.matches("^[a-zA-Z ]{2,50}$");
    }

    /**
     * Check if string is not null or empty
     * @param str String to check
     * @return true if not null or empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
