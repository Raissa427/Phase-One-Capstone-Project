package com.igirepay.lab2.daoImpl;

import com.igirepay.lab2.dao.ProcessedRequestDAO;
import com.igirepay.lab2.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Implements ProcessedRequestDAO using pure JDBC SQL.
 * Used to detect and prevent duplicate transactions.
 */
public class ProcessedRequestDAOImpl implements ProcessedRequestDAO {

    /** Returns true if the given reference ID already exists in processed_requests. */
    @Override
    public boolean existsByReferenceId(String referenceId) throws Exception {
        String sql = "SELECT id FROM processed_requests WHERE reference_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, referenceId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if a row was found
        }
    }

    /** Inserts a reference ID into processed_requests to mark it as done. */
    @Override
    public void save(String referenceId) throws Exception {
        String sql = "INSERT INTO processed_requests (reference_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, referenceId);
            stmt.executeUpdate();
        }
    }
}
