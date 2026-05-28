package com.igirepay.lab2.dao;

import com.igirepay.lab1.model.Account;

import java.util.List;

/**
 * Defines all database operations for the Account entity.
 * Implementations must use pure SQL — no business logic here.
 */
public interface AccountDAO {

    /** Saves a new account to the database. */
    void create(Account account) throws Exception;

    /** Finds an account by its unique ID. */
    Account findById(int id) throws Exception;

    /** Returns all accounts in the system. */
    List<Account> findAll() throws Exception;

    /** Updates the account's active status. */
    void update(Account account) throws Exception;

    /** Deletes an account by its ID. */
    void delete(int id) throws Exception;

    /** Returns all accounts belonging to a specific customer. */
    List<Account> findByCustomerId(int customerId) throws Exception;

    /** Updates the balance of a specific account. */
    void updateBalance(int accountId, double newBalance) throws Exception;

    /** Sets the active/inactive status of an account. */
    void setActive(int accountId, boolean active) throws Exception;
}
