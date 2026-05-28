package com.igirepay.lab2.daoImpl;

import com.igirepay.lab1.model.Transaction;
import com.igirepay.lab2.dao.TransactionDAO;
import com.igirepay.lab2.db.DatabaseConnection;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements TransactionDAO using pure JDBC SQL.
 * This class only runs SQL — no business logic allowed here.
 */
public class TransactionDAOImpl implements TransactionDAO {

    /** Inserts a new transaction row into the transactions table. */
    @Override
    public void create(Transaction transaction) throws Exception {
        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getAccountId());
            stmt.setString(2, transaction.getReferenceId());
            stmt.setString(3, transaction.getTransactionType());
            stmt.setDouble(4, transaction.getAmount());
            stmt.executeUpdate();

            // Read the auto-generated ID back into the transaction object
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                transaction.setId(keys.getInt(1));
            }
        }
    }

    /** Finds and returns a transaction by its ID, or null if not found. */
    @Override
    public Transaction findById(int id) throws Exception {
        String sql = "SELECT * FROM transactions WHERE id = ?";
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

    /** Returns a list of all transactions in the database. */
    @Override
    public List<Transaction> findAll() throws Exception {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapRow(rs));
            }
        }
        return transactions;
    }

    /** Deletes a transaction row by its ID. */
    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /** Returns all transactions for a specific account, newest first. */
    @Override
    public List<Transaction> findByAccountId(int accountId) throws Exception {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapRow(rs));
            }
        }
        return transactions;
    }

    /**
     * Exports all transactions for an account to a CSV file.
     * Writes a header row followed by one row per transaction.
     */
    @Override
    public void exportToCSV(int accountId, String filePath) throws Exception {
        List<Transaction> transactions = findByAccountId(accountId);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write the CSV header
            writer.println("ID,Reference ID,Type,Amount,Date");

            // Write one line per transaction
            for (Transaction t : transactions) {
                writer.printf("%d,%s,%s,%.2f,%s%n",
                        t.getId(),
                        t.getReferenceId(),
                        t.getTransactionType(),
                        t.getAmount(),
                        t.getCreatedAt().toString());
            }
        }
    }

    /** Maps a single database row to a Transaction object. */
    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setReferenceId(rs.getString("reference_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return transaction;
    }
}
