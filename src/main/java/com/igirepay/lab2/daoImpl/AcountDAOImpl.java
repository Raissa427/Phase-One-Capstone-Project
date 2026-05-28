package com.igirepay.lab2.daoImpl;

import com.igirepay.lab1.model.Account;
import com.igirepay.lab1.model.SavingsAccount;
import com.igirepay.lab1.model.WalletAccount;
import com.igirepay.lab2.dao.AccountDAO;
import com.igirepay.lab2.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements AccountDAO using pure JDBC SQL.
 * This class only runs SQL — no business logic allowed here.
 */
public class AcountDAOImpl implements AccountDAO {

    /** Inserts a new account row into the accounts table. */
    @Override
    public void create(Account account) throws Exception {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, account.getCustomerId());
            stmt.setString(2, account.getAccountType());
            stmt.setDouble(3, account.getBalance());
            stmt.executeUpdate();

            // Read the auto-generated ID back into the account object
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                account.setId(keys.getInt(1));
            }
        }
    }

    /** Finds and returns an account by its ID, or null if not found. */
    @Override
    public Account findById(int id) throws Exception {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns a list of all accounts in the database. */
    @Override
    public List<Account> findAll() throws Exception {
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        }
        return accounts;
    }

    /** Updates the active status of an account. */
    @Override
    public void update(Account account) throws Exception {
        String sql = "UPDATE accounts SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, account.isActive());
            stmt.setInt(2, account.getId());
            stmt.executeUpdate();
        }
    }

    /** Deletes an account row by its ID. */
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /** Returns all accounts belonging to a specific customer. */
    @Override
    public List<Account> findByCustomerId(int customerId) throws Exception {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        }
        return accounts;
    }

    /** Updates the balance column for a specific account. */
    @Override
    public void updateBalance(int accountId, double newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    /** Sets the is_active flag for a specific account. */
    @Override
    public void setActive(int accountId, boolean active) throws Exception {
        String sql = "UPDATE accounts SET is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a single database row to the correct Account subclass
     * based on the account_type column value.
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        Account account;

        if ("SAVINGS".equals(type)) {
            account = new SavingsAccount();
        } else {
            account = new WalletAccount();
        }

        account.setId(rs.getInt("id"));
        account.setCustomerId(rs.getInt("customer_id"));
        account.setAccountType(type);
        account.setBalance(rs.getDouble("balance"));
        account.setLoanBalance(rs.getDouble("loan_balance"));
        account.setSavingsBalance(rs.getDouble("savings_balance"));
        account.setActive(rs.getBoolean("is_active"));
        account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return account;
    }
}
