package com.igirepay.lab1.model;

import java.time.LocalDateTime;

/**
 * Represents a registered customer in the IgirePay system.
 * Maps directly to the 'customers' table in the database.
 */
public class Customer {

    private int           id;
    private String        fullName;
    private String        email;
    private String        phoneNumber;
    private String        pinHash;
    private String        role;          // "USER" or "ADMIN"
    private boolean       isActive;
    private int           failedAttempts;
    private boolean       isLocked;
    private LocalDateTime createdAt;

    // Default constructor — needed when building a Customer from a database result
    public Customer() {}

    // Full constructor — used when creating a new customer object with all fields
    public Customer(int id, String fullName, String email, String phoneNumber,
                    String pinHash, String role, boolean isActive,
                    int failedAttempts, boolean isLocked, LocalDateTime createdAt) {
        this.id             = id;
        this.fullName       = fullName;
        this.email          = email;
        this.phoneNumber    = phoneNumber;
        this.pinHash        = pinHash;
        this.role           = role;
        this.isActive       = isActive;
        this.failedAttempts = failedAttempts;
        this.isLocked       = isLocked;
        this.createdAt      = createdAt;
    }

    // --- Getters ---
    public int           getId()             { return id; }
    public String        getFullName()       { return fullName; }
    public String        getEmail()          { return email; }
    public String        getPhoneNumber()    { return phoneNumber; }
    public String        getPinHash()        { return pinHash; }
    public String        getRole()           { return role; }
    public boolean       isActive()          { return isActive; }
    public int           getFailedAttempts() { return failedAttempts; }
    public boolean       isLocked()          { return isLocked; }
    public LocalDateTime getCreatedAt()      { return createdAt; }

    // --- Setters ---
    public void setId(int id)                          { this.id             = id; }
    public void setFullName(String fullName)           { this.fullName       = fullName; }
    public void setEmail(String email)                 { this.email          = email; }
    public void setPhoneNumber(String phoneNumber)     { this.phoneNumber    = phoneNumber; }
    public void setPinHash(String pinHash)             { this.pinHash        = pinHash; }
    public void setRole(String role)                   { this.role           = role; }
    public void setActive(boolean active)              { this.isActive       = active; }
    public void setFailedAttempts(int failedAttempts)  { this.failedAttempts = failedAttempts; }
    public void setLocked(boolean locked)              { this.isLocked       = locked; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt      = createdAt; }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name=" + fullName + ", phone=" + phoneNumber + ", role=" + role + "}";
    }
}
