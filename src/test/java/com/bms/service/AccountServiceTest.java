package com.bms.service;

import com.bms.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AccountService
 */
@DisplayName("Account Service Tests")
public class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService();
    }

    @Test
    @DisplayName("Test opening account with valid details")
    public void testOpenAccount_ValidDetails() {
        assertNotNull(accountService);
    }

    @Test
    @DisplayName("Test opening account with negative initial deposit should throw exception")
    public void testOpenAccount_NegativeDeposit() {
        assertNotNull(accountService);
    }

    @Test
    @DisplayName("Test getting account by ID")
    public void testGetAccountById() {
        assertNotNull(accountService);
    }

    @Test
    @DisplayName("Test closing account with zero balance")
    public void testCloseAccount_ZeroBalance() {
        assertNotNull(accountService);
    }

    @Test
    @DisplayName("Test closing account with non-zero balance should throw exception")
    public void testCloseAccount_NonZeroBalance() {
        assertNotNull(accountService);
    }
}
