package com.igirepay.lab2.dao;

import com.igirepay.lab1.model.Customer;

import java.util.List;

/**
 * Defines all database operations for the Customer entity.
 * Implementations must use pure SQL — no business logic here.
 */
public interface CustomerDAO {

    /** Saves a new customer to the database. */
    void create(Customer customer) throws Exception;

    /** Finds a customer by their unique ID. */
    Customer findById(int id) throws Exception;

    /** Returns all customers in the system. */
    List<Customer> findAll() throws Exception;

    /** Updates the customer's name and email. */
    void update(Customer customer) throws Exception;

    /** Deletes a customer by their ID. */
    void delete(int id) throws Exception;

    /** Finds a customer by their phone number. */
    Customer findByPhone(String phone) throws Exception;

    /** Finds a customer by their email address. */
    Customer findByEmail(String email) throws Exception;

    /** Updates the number of failed PIN attempts for a customer. */
    void updateFailedAttempts(int id, int attempts) throws Exception;

    /** Locks or unlocks a customer account. */
    void setLocked(int id, boolean locked) throws Exception;

    /** Updates the PIN hash for a customer. */
    void updatePinHash(int id, String newPinHash) throws Exception;
}
