package com.igirepay.lab1.model;

import java.time.LocalDateTime;

/**
 * A Savings account with two special rules:
 *  1. You can only withdraw up to 50% of your balance per transaction.
 *  2. Every withdrawal has a 2% fee deducted on top.
 */
public class SavingsAccount extends Account {

    private static final double WITHDRAWAL_FEE_RATE  = 0.02; // 2%
    private static final double MAX_WITHDRAWAL_RATIO = 0.50; // 50%

    public SavingsAccount() {
        this.accountType = "SAVINGS";
    }

    public SavingsAccount(int id, int customerId, double balance,
                          double loanBalance, double savingsBalance,
                          boolean isActive, LocalDateTime createdAt) {
        super(id, customerId, "SAVINGS", balance, loanBalance, savingsBalance, isActive, createdAt);
    }

    /** Adds the amount directly to the savings balance. */
    @Override
    public void deposit(double amount) {
        this.balance += amount;
    }

    /**
     * Deducts the amount plus a 2% fee from the savings balance.
     * The caller must ensure the amount does not exceed 50% of balance.
     */
    @Override
    public void withdraw(double amount) {
        double fee          = amount * WITHDRAWAL_FEE_RATE;
        double totalDeducted = amount + fee;
        this.balance -= totalDeducted;
    }

    /** Returns the maximum amount allowed to withdraw in one transaction. */
    public double getMaxWithdrawable() {
        return this.balance * MAX_WITHDRAWAL_RATIO;
    }

    /** Routes the transaction to deposit or withdraw based on type. */
    @Override
    public void processTransaction(String type, double amount) {
        if (type.equals("DEPOSIT")) {
            deposit(amount);
        } else {
            withdraw(amount);
        }
    }
}
