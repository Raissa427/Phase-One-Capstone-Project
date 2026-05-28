package com.igirepay.lab1.model;

import java.time.LocalDateTime;

/**
 * A Wallet account — the main account for instant transfers.
 * No fees, no withdrawal limits.
 */
public class WalletAccount extends Account {

    public WalletAccount() {
        this.accountType = "WALLET";
    }

    public WalletAccount(int id, int customerId, double balance,
                         double loanBalance, double savingsBalance,
                         boolean isActive, LocalDateTime createdAt) {
        super(id, customerId, "WALLET", balance, loanBalance, savingsBalance, isActive, createdAt);
    }

    /** Adds the amount directly to the wallet balance. */
    @Override
    public void deposit(double amount) {
        this.balance += amount;
    }

    /** Deducts the amount directly from the wallet balance — no fee. */
    @Override
    public void withdraw(double amount) {
        this.balance -= amount;
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
