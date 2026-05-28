package com.igirepay.lab2.service;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.SavingsAccount;
import com.igirepay.lab1.model.WalletAccount;
import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab2.daoImpl.AcountDAOImpl;
import com.igirepay.lab2.daoImpl.CustomerDAOImpl;
import com.igirepay.lab3.exception.InvalidAccountException;

import java.util.List;

/**
 * Handles all business logic related to accounts:
 * creating, retrieving, and managing wallet and savings accounts.
 */
public class AccountService {

    private final AccountDAO  accountDAO  = new AcountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    /**
     * Creates a new WALLET account for the given customer.
     * Verifies the customer exists before creating.
     */
    public Account createWalletAccount(int customerId) throws Exception {
        if (customerDAO.findById(customerId) == null) {
            throw new InvalidAccountException("Customer not found.");
        }

        WalletAccount wallet = new WalletAccount();
        wallet.setCustomerId(customerId);
        accountDAO.create(wallet);
        return wallet;
    }

    /**
     * Creates a new SAVINGS account for the given customer.
     * Verifies the customer exists before creating.
     */
    public Account createSavingsAccount(int customerId) throws Exception {
        if (customerDAO.findById(customerId) == null) {
            throw new InvalidAccountException("Customer not found.");
        }

        SavingsAccount savings = new SavingsAccount();
        savings.setCustomerId(customerId);
        accountDAO.create(savings);
        return savings;
    }

    /**
     * Returns the current balance of an account.
     * Throws an exception if the account does not exist.
     */
    public double getBalance(int accountId) throws Exception {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found.");
        }
        return account.getBalance();
    }

    /**
     * Deletes an inactive account.
     * Only allowed if the account balance is zero.
     */
    public void deleteInactiveAccount(int accountId) throws Exception {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found.");
        }
        if (account.getBalance() > 0) {
            throw new InvalidAccountException("Cannot delete an account that still has a balance.");
        }
        accountDAO.delete(accountId);
    }

    /**
     * Returns all accounts belonging to a specific customer.
     */
    public List<Account> getCustomerAccounts(int customerId) throws Exception {
        return accountDAO.findByCustomerId(customerId);
    }

    /**
     * Returns all accounts in the system — used by admin only.
     */
    public List<Account> getAllAccounts() throws Exception {
        return accountDAO.findAll();
    }

    /**
     * Locks or unlocks an account — used by admin only.
     */
    public void setAccountActive(int accountId, boolean active) throws Exception {
        accountDAO.setActive(accountId, active);
    }

    /**
     * Returns a single account by its ID.
     */
    public Account getAccountById(int accountId) throws Exception {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found.");
        }
        return account;
    }
}
