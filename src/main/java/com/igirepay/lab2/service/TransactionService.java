package com.igirepay.lab2.service;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.SavingsAccount;
import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab2.dao.ProcessedRequestDAO;
import com.igirepay.lab2.dao.TransactionDAO;
import com.igirepay.lab2.daoImpl.AcountDAOImpl;
import com.igirepay.lab2.daoImpl.CustomerDAOImpl;
import com.igirepay.lab2.daoImpl.ProcessedRequestDAOImpl;
import com.igirepay.lab2.daoImpl.TransactionDAOImpl;
import com.igirepay.lab2.db.DatabaseConnection;
import com.igirepay.lab3.exception.DuplicateTransactionException;
import com.igirepay.lab3.exception.InsufficientBalanceException;
import com.igirepay.lab3.exception.InvalidAccountException;
import com.igirepay.lab3.exception.InvalidAmountException;

import java.sql.Connection;
import java.util.List;

/**
 * Handles all business logic for financial transactions:
 * deposit, withdrawal, transfer, history, and CSV export.
 */
public class TransactionService {

    private final AccountDAO          accountDAO          = new AcountDAOImpl();
    private final TransactionDAO      transactionDAO      = new TransactionDAOImpl();
    private final ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAOImpl();
    private final CustomerDAO         customerDAO         = new CustomerDAOImpl();

    /**
     * Deposits money into an account.
     * Validates the amount, checks for duplicate reference,
     * updates the balance, records the transaction, and marks the reference as processed.
     */
    public void deposit(int accountId, double amount, String referenceId) throws Exception {
        // Amount must be positive
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }

        // Prevent duplicate transactions
        if (processedRequestDAO.existsByReferenceId(referenceId)) {
            throw new DuplicateTransactionException("This transaction has already been processed.");
        }

        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found.");
        }

        // Update balance and record the transaction
        double newBalance = account.getBalance() + amount;
        accountDAO.updateBalance(accountId, newBalance);
        saveTransaction(accountId, referenceId, "DEPOSIT", amount);
        processedRequestDAO.save(referenceId);
    }

    /**
     * Withdraws money from an account.
     * Validates amount, checks balance, applies savings rules if needed,
     * updates balance, records the transaction, and marks the reference as processed.
     */
    public void withdraw(int accountId, double amount, String referenceId) throws Exception {
        // Amount must be positive
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }

        // Prevent duplicate transactions
        if (processedRequestDAO.existsByReferenceId(referenceId)) {
            throw new DuplicateTransactionException("This transaction has already been processed.");
        }

        Account account = accountDAO.findById(accountId);
        if (account == null) {
            throw new InvalidAccountException("Account not found.");
        }

        double totalDeducted = amount;

        // Apply savings account rules: 50% limit and 2% fee
        if (account instanceof SavingsAccount savingsAccount) {
            if (amount > savingsAccount.getMaxWithdrawable()) {
                throw new InsufficientBalanceException(
                        "Savings accounts allow a maximum withdrawal of 50% of balance per transaction.");
            }
            double fee = amount * 0.02;
            totalDeducted = amount + fee;
        }

        // Check that the account has enough balance
        if (account.getBalance() < totalDeducted) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        // Update balance and record the transaction
        double newBalance = account.getBalance() - totalDeducted;
        accountDAO.updateBalance(accountId, newBalance);
        saveTransaction(accountId, referenceId, "WITHDRAWAL", amount);
        processedRequestDAO.save(referenceId);
    }

    /**
     * Transfers money from one customer's wallet to another customer's wallet.
     * Uses a JDBC transaction so both balance updates succeed or both are rolled back.
     */
    public void transfer(Customer sender, String receiverPhone,
                         double amount, String referenceId) throws Exception {
        // Amount must be positive
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }

        // Prevent duplicate transactions
        if (processedRequestDAO.existsByReferenceId(referenceId)) {
            throw new DuplicateTransactionException("This transaction has already been processed.");
        }

        // Find the sender's wallet account
        Account senderAccount = getWalletAccount(sender.getId());

        // Find the receiver by phone number
        Customer receiver = customerDAO.findByPhone(receiverPhone);
        if (receiver == null) {
            throw new InvalidAccountException("Receiver phone number not found.");
        }

        // Find the receiver's wallet account
        Account receiverAccount = getWalletAccount(receiver.getId());

        // Check that the sender has enough balance
        if (senderAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance for transfer.");
        }

        // Use a JDBC transaction so both updates happen together or not at all
        Connection conn = DatabaseConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Deduct from sender
            accountDAO.updateBalance(senderAccount.getId(), senderAccount.getBalance() - amount);

            // Add to receiver
            accountDAO.updateBalance(receiverAccount.getId(), receiverAccount.getBalance() + amount);

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }

        // Record a transaction for both the sender and receiver
        saveTransaction(senderAccount.getId(),   referenceId + "-SEND", "TRANSFER", amount);
        saveTransaction(receiverAccount.getId(), referenceId + "-RECV", "TRANSFER", amount);
        processedRequestDAO.save(referenceId);
    }

    /**
     * Returns the full transaction history for a given account.
     */
    public List<Transaction> getTransactionHistory(int accountId) throws Exception {
        return transactionDAO.findByAccountId(accountId);
    }

    /**
     * Returns all transactions in the system — used by admin only.
     */
    public List<Transaction> getAllTransactions() throws Exception {
        return transactionDAO.findAll();
    }

    /**
     * Exports the transaction history for an account to a CSV file.
     */
    public void exportToCSV(int accountId, String filePath) throws Exception {
        transactionDAO.exportToCSV(accountId, filePath);
    }

    /**
     * Helper: finds the WALLET account for a given customer.
     * Throws an exception if no wallet account is found.
     */
    private Account getWalletAccount(int customerId) throws Exception {
        List<Account> accounts = accountDAO.findByCustomerId(customerId);
        for (Account account : accounts) {
            if ("WALLET".equals(account.getAccountType())) {
                return account;
            }
        }
        throw new InvalidAccountException("No wallet account found for customer ID: " + customerId);
    }

    /**
     * Helper: creates and saves a Transaction record to the database.
     */
    private void saveTransaction(int accountId, String referenceId,
                                  String type, double amount) throws Exception {
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setReferenceId(referenceId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transactionDAO.create(transaction);
    }
}
