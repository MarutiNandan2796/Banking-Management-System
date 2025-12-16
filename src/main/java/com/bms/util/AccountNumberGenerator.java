package com.bms.util;

import java.util.Random;

/**
 * Utility class for generating account numbers
 */
public class AccountNumberGenerator {
    
    private static final Random random = new Random();
    private static final String PREFIX = "ACC";

    /**
     * Generate a unique 12-digit account number
     * Format: ACC + 9 digits
     * @return Generated account number
     */
    public static String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder(PREFIX);
        for (int i = 0; i < 9; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    /**
     * Validate account number format
     * @param accountNumber Account number to validate
     * @return true if valid format
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return false;
        }
        return accountNumber.matches("ACC\\d{9}");
    }
}
