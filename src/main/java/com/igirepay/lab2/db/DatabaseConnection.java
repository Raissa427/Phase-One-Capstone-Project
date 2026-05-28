package com.igirepay.lab2.db;

import com.igirepay.lab3.exception.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class that manages the single database connection.
 * Only one connection is created and reused throughout the app.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/igirepay_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "Raissa@Rwanda20";

    // The single shared connection instance
    private static Connection connection;

    // Private constructor — no one can create an instance from outside
    private DatabaseConnection() {}

    /**
     * Returns the shared connection, creating it if it does not exist yet.
     */
    public static Connection getConnection() throws DatabaseConnectionException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Could not connect to the database: " + e.getMessage());
        }
    }
}
