package com.bms.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ValidationUtil
 */
@DisplayName("Validation Utility Tests")
public class ValidationUtilTest {

    @Test
    @DisplayName("Test valid email format")
    public void testIsValidEmail_Valid() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.in"));
    }

    @Test
    @DisplayName("Test invalid email format")
    public void testIsValidEmail_Invalid() {
        assertFalse(ValidationUtil.isValidEmail("invalid.email"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("test@"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    @DisplayName("Test valid phone number")
    public void testIsValidPhone_Valid() {
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
    }

    @Test
    @DisplayName("Test invalid phone number")
    public void testIsValidPhone_Invalid() {
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone("12345678901"));
        assertFalse(ValidationUtil.isValidPhone("abcd123456"));
        assertFalse(ValidationUtil.isValidPhone(null));
    }

    @Test
    @DisplayName("Test valid username format")
    public void testIsValidUsername_Valid() {
        assertTrue(ValidationUtil.isValidUsername("user123"));
        assertTrue(ValidationUtil.isValidUsername("test_user"));
        assertTrue(ValidationUtil.isValidUsername("JohnDoe"));
    }

    @Test
    @DisplayName("Test invalid username format")
    public void testIsValidUsername_Invalid() {
        assertFalse(ValidationUtil.isValidUsername("ab"));  // Too short
        assertFalse(ValidationUtil.isValidUsername("verylongusernamethatexceedslimit"));  // Too long
        assertFalse(ValidationUtil.isValidUsername("user@123"));  // Special char
        assertFalse(ValidationUtil.isValidUsername(null));
    }

    @Test
    @DisplayName("Test valid name format")
    public void testIsValidName_Valid() {
        assertTrue(ValidationUtil.isValidName("John"));
        assertTrue(ValidationUtil.isValidName("John Doe"));
        assertTrue(ValidationUtil.isValidName("Mary Jane"));
    }

    @Test
    @DisplayName("Test invalid name format")
    public void testIsValidName_Invalid() {
        assertFalse(ValidationUtil.isValidName("J"));  // Too short
        assertFalse(ValidationUtil.isValidName("John123"));  // Contains digits
        assertFalse(ValidationUtil.isValidName("John@Doe"));  // Special char
        assertFalse(ValidationUtil.isValidName(null));
    }

    @Test
    @DisplayName("Test isNotEmpty with valid strings")
    public void testIsNotEmpty_Valid() {
        assertTrue(ValidationUtil.isNotEmpty("test"));
        assertTrue(ValidationUtil.isNotEmpty("   test   "));
    }

    @Test
    @DisplayName("Test isNotEmpty with invalid strings")
    public void testIsNotEmpty_Invalid() {
        assertFalse(ValidationUtil.isNotEmpty(null));
        assertFalse(ValidationUtil.isNotEmpty(""));
        assertFalse(ValidationUtil.isNotEmpty("   "));
    }
}
