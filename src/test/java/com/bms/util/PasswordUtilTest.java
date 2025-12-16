package com.bms.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PasswordUtil
 */
@DisplayName("Password Utility Tests")
public class PasswordUtilTest {

    @Test
    @DisplayName("Test password hashing")
    public void testHashPassword() {
        String plainPassword = "Test@123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.length() > 0);
    }

    @Test
    @DisplayName("Test password verification with correct password")
    public void testVerifyPassword_Correct() {
        String plainPassword = "Test@123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        assertTrue(PasswordUtil.verifyPassword(plainPassword, hashedPassword));
    }

    @Test
    @DisplayName("Test password verification with incorrect password")
    public void testVerifyPassword_Incorrect() {
        String plainPassword = "Test@123";
        String wrongPassword = "Wrong@123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        assertFalse(PasswordUtil.verifyPassword(wrongPassword, hashedPassword));
    }

    @Test
    @DisplayName("Test strong password validation - valid password")
    public void testIsPasswordStrong_Valid() {
        String strongPassword = "Strong@123";
        assertTrue(PasswordUtil.isPasswordStrong(strongPassword));
    }

    @Test
    @DisplayName("Test strong password validation - too short")
    public void testIsPasswordStrong_TooShort() {
        String shortPassword = "Sh@1";
        assertFalse(PasswordUtil.isPasswordStrong(shortPassword));
    }

    @Test
    @DisplayName("Test strong password validation - no uppercase")
    public void testIsPasswordStrong_NoUppercase() {
        String noUpperPassword = "test@123";
        assertFalse(PasswordUtil.isPasswordStrong(noUpperPassword));
    }

    @Test
    @DisplayName("Test strong password validation - no lowercase")
    public void testIsPasswordStrong_NoLowercase() {
        String noLowerPassword = "TEST@123";
        assertFalse(PasswordUtil.isPasswordStrong(noLowerPassword));
    }

    @Test
    @DisplayName("Test strong password validation - no digit")
    public void testIsPasswordStrong_NoDigit() {
        String noDigitPassword = "Test@Test";
        assertFalse(PasswordUtil.isPasswordStrong(noDigitPassword));
    }

    @Test
    @DisplayName("Test strong password validation - no special character")
    public void testIsPasswordStrong_NoSpecialChar() {
        String noSpecialPassword = "Test1234";
        assertFalse(PasswordUtil.isPasswordStrong(noSpecialPassword));
    }

    @Test
    @DisplayName("Test hash password with null should throw exception")
    public void testHashPassword_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword(null);
        });
    }

    @Test
    @DisplayName("Test hash password with empty string should throw exception")
    public void testHashPassword_Empty() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.hashPassword("");
        });
    }
}
