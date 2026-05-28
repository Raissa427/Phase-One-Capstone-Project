package com.igirepay.lab3.exception;

/** Thrown when an account cannot be found or is inactive. */
public class InvalidAccountException extends Exception {
    public InvalidAccountException(String message) {
        super(message);
    }
}
