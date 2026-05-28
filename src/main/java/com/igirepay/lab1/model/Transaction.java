package com.igirepay.lab1.model;

import java.time.LocalDateTime;

/**
 * Represents a single financial transaction.
 * Maps directly to the 'transactions' table in the database.
 */
public class Transaction {

    private int           id;
    private int           accountId;
    private String        referenceId;
    private String        transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER
    private double        amount;
    private LocalDateTime createdAt;

    // Default constructor
    public Transaction() {}

    // Full constructor
    public Transaction(int id, int accountId, String referenceId,
                       String transactionType, double amount, LocalDateTime createdAt) {
        this.id              = id;
        this.accountId       = accountId;
        this.referenceId     = referenceId;
        this.transactionType = transactionType;
        this.amount          = amount;
        this.createdAt       = createdAt;
    }

    // --- Getters ---
    public int           getId()              { return id; }
    public int           getAccountId()       { return accountId; }
    public String        getReferenceId()     { return referenceId; }
    public String        getTransactionType() { return transactionType; }
    public double        getAmount()          { return amount; }
    public LocalDateTime getCreatedAt()       { return createdAt; }

    // --- Setters ---
    public void setId(int id)                           { this.id              = id; }
    public void setAccountId(int accountId)             { this.accountId       = accountId; }
    public void setReferenceId(String referenceId)      { this.referenceId     = referenceId; }
    public void setTransactionType(String type)         { this.transactionType = type; }
    public void setAmount(double amount)                { this.amount          = amount; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt       = createdAt; }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", type=" + transactionType + ", amount=" + amount + "}";
    }
}
