package com.igirepay.lab2.service;

import com.igirepay.lab1.model.Customer;
import com.igirepay.lab1.model.WalletAccount;
import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab2.daoImpl.AcountDAOImpl;
import com.igirepay.lab2.daoImpl.CustomerDAOImpl;
import com.igirepay.lab3.exception.AccountLockedException;
import com.igirepay.lab3.exception.InvalidAccountException;

import java.security.MessageDigest;
import java.util.List;

/**
 * Handles all business logic related to customers:
 * registration, login, profile updates, and PIN changes.
 */
public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AccountDAO  accountDAO  = new AcountDAOImpl();

    private static final int MAX_FAILED_ATTEMPTS = 3;

    /**
     * Registers a new customer.
     * Validates all fields, checks for duplicate phone/email,
     * hashes the PIN, saves the customer, and auto-creates a WALLET account.
     */
    public Customer registerCustomer(String fullName, String email,
                                     String phone, String pin, String role) throws Exception {
        // Validate that no field is empty
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || pin.isBlank()) {
            throw new InvalidAccountException("All fields are required.");
        }

        // PIN must be exactly 5 digits
        if (!pin.matches("\\d{5}")) {
            throw new InvalidAccountException("PIN must be exactly 5 digits.");
        }

        // Check that the phone number is not already registered
        if (customerDAO.findByPhone(phone) != null) {
            throw new InvalidAccountException("Phone number is already registered.");
        }

        // Check that the email is not already registered
        if (customerDAO.findByEmail(email) != null) {
            throw new InvalidAccountException("Email is already registered.");
        }

        // Build and save the new customer
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        customer.setPinHash(hashPin(pin));
        customer.setRole(role != null ? role : "USER");
        customerDAO.create(customer);

        // Auto-create a WALLET account for the new customer
        WalletAccount wallet = new WalletAccount();
        wallet.setCustomerId(customer.getId());
        accountDAO.create(wallet);

        return customer;
    }

    /**
     * Logs in a customer by phone and PIN.
     * Checks if the account is locked, verifies the PIN,
     * resets failed attempts on success, increments them on failure,
     * and locks the account after 3 failed attempts.
     */
    public Customer login(String phone, String pin) throws Exception {
        Customer customer = customerDAO.findByPhone(phone);

        // Check that the customer exists
        if (customer == null) {
            throw new InvalidAccountException("No account found with that phone number.");
        }

        // Check if the account is locked
        if (customer.isLocked()) {
            throw new AccountLockedException("Your account is locked. Please contact support.");
        }

        // Verify the PIN by comparing hashes
        if (!customer.getPinHash().equals(hashPin(pin))) {
            int newAttempts = customer.getFailedAttempts() + 1;
            customerDAO.updateFailedAttempts(customer.getId(), newAttempts);

            // Lock the account if max attempts reached
            if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                customerDAO.setLocked(customer.getId(), true);
                throw new AccountLockedException("Too many failed attempts. Account is now locked.");
            }

            throw new InvalidAccountException("Incorrect PIN. " + (MAX_FAILED_ATTEMPTS - newAttempts) + " attempt(s) remaining.");
        }

        // Reset failed attempts on successful login
        customerDAO.updateFailedAttempts(customer.getId(), 0);
        return customer;
    }

    /**
     * Updates a customer's full name and email.
     * Validates that the fields are not empty before saving.
     */
    public void updateCustomerInfo(Customer customer, String newName, String newEmail) throws Exception {
        if (newName.isBlank() || newEmail.isBlank()) {
            throw new InvalidAccountException("Name and email cannot be empty.");
        }
        customer.setFullName(newName);
        customer.setEmail(newEmail);
        customerDAO.update(customer);
    }

    /**
     * Changes a customer's PIN.
     * Verifies the old PIN first, then saves the new hashed PIN.
     */
    public void changePin(Customer customer, String oldPin, String newPin) throws Exception {
        // Verify the old PIN is correct
        if (!customer.getPinHash().equals(hashPin(oldPin))) {
            throw new InvalidAccountException("Old PIN is incorrect.");
        }

        if (newPin.isBlank()) {
            throw new InvalidAccountException("New PIN cannot be empty.");
        }

        // New PIN must be exactly 5 digits
        if (!newPin.matches("\\d{5}")) {
            throw new InvalidAccountException("PIN must be exactly 5 digits.");
        }

        String newHash = hashPin(newPin);
        customerDAO.updatePinHash(customer.getId(), newHash);
        customer.setPinHash(newHash);
    }

    /** Returns all customers — used by admin only. */
    public List<Customer> getAllCustomers() throws Exception {
        return customerDAO.findAll();
    }

    /** Locks or unlocks a customer account — used by admin only. */
    public void setAccountLocked(int customerId, boolean locked) throws Exception {
        customerDAO.setLocked(customerId, locked);
    }

    /**
     * Hashes a plain-text PIN using SHA-256.
     * Returns the hex string of the hash.
     */
    public static String hashPin(String pin) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(pin.getBytes());

        // Convert each byte to a two-character hex string
        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
