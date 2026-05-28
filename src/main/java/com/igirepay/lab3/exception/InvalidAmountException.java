package com.igirepay.lab3.exception;

/** Thrown when the amount provided for a transaction is zero or negative. */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}
