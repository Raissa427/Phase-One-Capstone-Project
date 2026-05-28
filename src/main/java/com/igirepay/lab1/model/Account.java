package com.igirepay.lab1.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all account types (Wallet and Savings).
 * Defines the common fields and forces subclasses to implement
 * deposit, withdraw, and processTransaction.
 */
public abstract class Account {

    protected int           id;
    protected int           customerId;
    protected String        accountType;
    protected double        balance;
    protected double        loanBalance;
    protected double        savingsBalance;
    protected boolean       isActive;
    protected LocalDateTime createdAt;

    // Default constructor
    public Account() {}

    // Full constructor
    public Account(int id, int customerId, String accountType, double balance,
                   double loanBalance, double savingsBalance,
                   boolean isActive, LocalDateTime createdAt) {
        this.id             = id;
        this.customerId     = customerId;
        this.accountType    = accountType;
        this.balance        = balance;
        this.loanBalance    = loanBalance;
        this.savingsBalance = savingsBalance;
        this.isActive       = isActive;
        this.createdAt      = createdAt;
    }

    // --- Abstract methods that each account type must implement ---

    /** Adds the given amount to this account's balance. */
    public abstract void deposit(double amount);

    /** Deducts the given amount from this account's balance. */
    public abstract void withdraw(double amount);

    /** Handles a transaction of the given type and amount. */
    public abstract void processTransaction(String type, double amount);

    // --- Getters ---
    public int           getId()             { return id; }
    public int           getCustomerId()     { return customerId; }
    public String        getAccountType()    { return accountType; }
    public double        getBalance()        { return balance; }
    public double        getLoanBalance()    { return loanBalance; }
    public double        getSavingsBalance() { return savingsBalance; }
    public boolean       isActive()          { return isActive; }
    public LocalDateTime getCreatedAt()      { return createdAt; }

    // --- Setters ---
    public void setId(int id)                         { this.id             = id; }
    public void setCustomerId(int customerId)         { this.customerId     = customerId; }
    public void setAccountType(String accountType)    { this.accountType    = accountType; }
    public void setBalance(double balance)            { this.balance        = balance; }
    public void setLoanBalance(double loanBalance)    { this.loanBalance    = loanBalance; }
    public void setSavingsBalance(double savingsBalance) { this.savingsBalance = savingsBalance; }
    public void setActive(boolean active)             { this.isActive       = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt      = createdAt; }

    @Override
    public String toString() {
        return "Account{id=" + id + ", type=" + accountType + ", balance=" + balance + "}";
    }
}
