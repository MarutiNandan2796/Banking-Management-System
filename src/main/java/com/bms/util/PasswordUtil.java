package com.bms.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt
 */
public class PasswordUtil {
    
    // BCrypt work factor (log2 of the number of rounds)
    private static final int WORK_FACTOR = 12;

    /**
     * Hash a password using BCrypt
     * @param plainPassword Plain text password
     * @return Hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verify a password against a hash
     * @param plainPassword Plain text password to verify
     * @param hashedPassword Previously hashed password
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets strength requirements
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    /**
     * Get password strength requirements message
     * @return String describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain:\n" +
               "- At least one uppercase letter\n" +
               "- At least one lowercase letter\n" +
               "- At least one digit\n" +
               "- At least one special character";
    }
}
