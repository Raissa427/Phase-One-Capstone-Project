package com.igirepay.lab3.exception;

/** Thrown when the application cannot connect to the database. */
public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
