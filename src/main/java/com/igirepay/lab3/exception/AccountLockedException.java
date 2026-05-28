package com.igirepay.lab3.exception;

/** Thrown when a customer account is locked due to too many failed PIN attempts. */
public class AccountLockedException extends Exception {
    public AccountLockedException(String message) {
        super(message);
    }
}
