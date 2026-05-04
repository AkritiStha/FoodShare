package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the users table.
 * All queries use PreparedStatement to prevent SQL injection.
 */
public class UserDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new user and returns the generated primary key.
     *
     * @param user populated User object (password must already be BCrypt-hashed)
     * @return generated user id, or -1 on failure
     */
    public int createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role, phone, address, approved) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            // Donors are auto-approved; NGOs need admin approval
            ps.setBoolean(7, "donor".equals(user.getRole()) || "admin".equals(user.getRole()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
        }
        return users;
    }

    /** Returns all users, ordered by creation date descending. */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        }
        return users;
    }

    /** Returns NGOs pending admin approval. */
    public List<User> findPendingNgos() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'ngo' AND approved = 0 ORDER BY created_at";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        }
        return users;
    }

    /** Counts total users with a given role. */
    public int countByRole(String role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Checks if an email already exists (for duplicate check on registration). */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void updateProfile(User user) throws SQLException {
        String sql = "UPDATE users SET name=?, phone=?, address=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAddress());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        }
    }

    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /** Admin: approve or reject an NGO registration. */
    public void setApproval(int userId, boolean approved) throws SQLException {
        String sql = "UPDATE users SET approved=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ── Row mapper ────────────────────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setApproved(rs.getBoolean("approved"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }
}
