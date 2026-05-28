package com.igirepay.lab2.dao;

/**
 * Defines database operations for tracking processed transaction reference IDs.
 * Used to prevent duplicate transactions from being processed twice.
 */
public interface ProcessedRequestDAO {

    /** Returns true if this reference ID has already been processed. */
    boolean existsByReferenceId(String referenceId) throws Exception;

    /** Saves a reference ID to mark a transaction as processed. */
    void save(String referenceId) throws Exception;
}
