package com.igirepay.lab2.daoImpl;

import com.igirepay.lab1.model.Customer;
import com.igirepay.lab2.dao.CustomerDAO;
import com.igirepay.lab2.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements CustomerDAO using pure JDBC SQL.
 * This class only runs SQL — no business logic allowed here.
 */
public class CustomerDAOImpl implements CustomerDAO {

    /** Inserts a new customer row into the customers table. */
    @Override
    public void create(Customer customer) throws Exception {
        String sql = "INSERT INTO customers (full_name, email, phone_number, pin_hash, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getPinHash());
            stmt.setString(5, customer.getRole());
            stmt.executeUpdate();

            // Read the auto-generated ID back into the customer object
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                customer.setId(keys.getInt(1));
            }
        }
    }

    /** Finds and returns a customer by their ID, or null if not found. */
    @Override
    public Customer findById(int id) throws Exception {
        String sql = "SELECT * FROM customers WHERE id = ?";
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

    /** Returns a list of all customers in the database. */
    @Override
    public List<Customer> findAll() throws Exception {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        }
        return customers;
    }

    /** Updates the full name and email of an existing customer. */
    @Override
    public void update(Customer customer) throws Exception {
        String sql = "UPDATE customers SET full_name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setInt(3, customer.getId());
            stmt.executeUpdate();
        }
    }

    /** Deletes a customer row by their ID. */
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /** Finds a customer by their phone number, or null if not found. */
    @Override
    public Customer findByPhone(String phone) throws Exception {
        String sql = "SELECT * FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    /** Finds a customer by their email address, or null if not found. */
    @Override
    public Customer findByEmail(String email) throws Exception {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    /** Updates the failed PIN attempt count for a customer. */
    @Override
    public void updateFailedAttempts(int id, int attempts) throws Exception {
        String sql = "UPDATE customers SET failed_attempts = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /** Locks or unlocks a customer account. */
    @Override
    public void setLocked(int id, boolean locked) throws Exception {
        String sql = "UPDATE customers SET is_locked = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, locked);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /** Updates the stored PIN hash for a customer. */
    @Override
    public void updatePinHash(int id, String newPinHash) throws Exception {
        String sql = "UPDATE customers SET pin_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPinHash);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /** Maps a single database row to a Customer object. */
    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setPinHash(rs.getString("pin_hash"));
        customer.setRole(rs.getString("role"));
        customer.setActive(rs.getBoolean("is_active"));
        customer.setFailedAttempts(rs.getInt("failed_attempts"));
        customer.setLocked(rs.getBoolean("is_locked"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return customer;
    }
}
