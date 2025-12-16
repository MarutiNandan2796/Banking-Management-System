package com.bms.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transaction entity representing a financial transaction
 */
public class Transaction {
    private Long transactionId;
    private Long accountId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private Long relatedAccountId; // For transfers
    private LocalDateTime transactionDate;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    }

    // Constructors
    public Transaction() {
    }

    public Transaction(Long accountId, TransactionType transactionType, BigDecimal amount, 
                      BigDecimal balanceAfter, String description) {
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(Long accountId, TransactionType transactionType, BigDecimal amount,
                      BigDecimal balanceAfter, String description, Long relatedAccountId) {
        this(accountId, transactionType, amount, balanceAfter, description);
        this.relatedAccountId = relatedAccountId;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRelatedAccountId() {
        return relatedAccountId;
    }

    public void setRelatedAccountId(Long relatedAccountId) {
        this.relatedAccountId = relatedAccountId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", balanceAfter=" + balanceAfter +
                ", description='" + description + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
