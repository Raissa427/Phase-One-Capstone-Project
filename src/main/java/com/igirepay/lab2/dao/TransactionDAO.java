package com.igirepay.lab2.dao;

import com.igirepay.lab1.model.Transaction;

import java.util.List;

/**
 * Defines all database operations for the Transaction entity.
 * Implementations must use pure SQL — no business logic here.
 */
public interface TransactionDAO {

    /** Saves a new transaction to the database. */
    void create(Transaction transaction) throws Exception;

    /** Finds a transaction by its unique ID. */
    Transaction findById(int id) throws Exception;

    /** Returns all transactions in the system. */
    List<Transaction> findAll() throws Exception;

    /** Deletes a transaction by its ID. */
    void delete(int id) throws Exception;

    /** Returns all transactions for a specific account. */
    List<Transaction> findByAccountId(int accountId) throws Exception;

    /** Exports all transactions for an account to a CSV file at the given path. */
    void exportToCSV(int accountId, String filePath) throws Exception;
}
