package com.igirepay.lab3.exception;

/** Thrown when an account does not have enough balance for the requested operation. */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
