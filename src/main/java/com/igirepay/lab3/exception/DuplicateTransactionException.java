package com.igirepay.lab3.exception;

/** Thrown when a transaction with the same reference ID has already been processed. */
public class DuplicateTransactionException extends Exception {
    public DuplicateTransactionException(String message) {
        super(message);
    }
}
