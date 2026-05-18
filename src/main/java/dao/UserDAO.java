package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for all CRUD operations on the
 * {@code users} database table in the FoodShare application.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the DAO layer, which is directly below the service
 * layer. It is the <em>only</em> class that issues SQL queries against the
 * {@code users} table. Higher-level classes — specifically
 * {@link service.UserService} — call the methods here to read and write user
 * data without needing to know any SQL details. This separation ensures that
 * if the database schema changes, only the DAO needs to be updated.
 *
 * <p><strong>SQL-injection prevention:</strong><br>
 * Every method that accepts user-supplied data uses {@link PreparedStatement}
 * with parameterised placeholders ({@code ?}), so no raw string concatenation
 * is performed with external input.
 *
 * <p><strong>Connection management:</strong><br>
 * Each method obtains a fresh connection from {@link DBConnection#getConnection()}
 * inside a try-with-resources block, guaranteeing that the connection,
 * statement, and result set are always closed — even when an exception occurs.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.UserService
 * @see     util.DBConnection
 * @see     model.User
 */

public class UserDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new user record into the {@code users} table and returns
     * the generated primary key.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code INSERT}
     * statement against the {@code users} table. The {@code approved} column
     * is derived from the user's role: donor and admin accounts are inserted
     * as approved ({@code 1}), while NGO accounts are inserted as unapproved
     * ({@code 0}) and require subsequent admin approval via
     * {@link #setApproval(int, boolean)}.
     *
     * <p><strong>Important:</strong> The {@code password} field on the supplied
     * {@link User} object must already be a BCrypt hash produced by
     * {@link util.PasswordUtil#hashPassword(String)} before this method is
     * called. Storing plain-text passwords is a security violation.
     *
     * @param user a populated {@link User} object containing the name, email,
     *             hashed password, role, phone, and address to persist;
     *             the {@code id} field is ignored and will be overwritten by
     *             the auto-generated key
     * @return the auto-generated {@code users.id} of the newly inserted row,
     *         or {@code -1} if the insert succeeded but no generated key was
     *         returned
     * @throws SQLException if a database access error occurs, or if a user
     *                      with the same email already exists (unique constraint
     *                      violation)
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

    /**
     * Retrieves a single user record by its primary key.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query on the {@code users} table filtered by {@code id}.
     * Used by {@link service.UserService#findById(int)} to refresh the session
     * user object after a profile update.
     *
     * @param id the primary key of the user to retrieve
     * @return the matching {@link User} object, or {@code null} if no user
     *         with the given {@code id} exists
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves a single user record by email address.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query filtered by the {@code email} column, which has a unique index.
     * This is the primary lookup method used during the login flow: the
     * service layer calls this method to obtain the stored BCrypt hash for
     * password verification.
     *
     * @param email the email address to search for; the lookup is
     *              case-sensitive at the database level but the application
     *              normalises emails to lowercase before storage
     * @return the matching {@link User} object, or {@code null} if no account
     *         with the given email exists
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves all user records that match a given role.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query filtered by the {@code role} column, ordered by
     * {@code created_at DESC} so the most recently registered users appear
     * first. Used by the admin panel to list all donors or all NGOs separately.
     *
     * @param role the role to filter by; one of {@code "donor"},
     *             {@code "ngo"}, or {@code "admin"}
     * @return a {@link List} of {@link User} objects with the specified role,
     *         ordered by registration date descending; never {@code null} but
     *         may be empty if no matching users exist
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves all user records in the system, ordered by registration date.
     *
     * <p><strong>Database interaction:</strong> Executes an unfiltered
     * {@code SELECT *} query on the {@code users} table, ordered by
     * {@code created_at DESC}. Called exclusively by the admin
     * {@code /admin/manageUsers} endpoint to populate the full user table.
     *
     * @return a {@link List} of all {@link User} objects, ordered by
     *         registration date descending; never {@code null}
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves all NGO accounts whose {@code approved} flag is {@code 0}
     * (pending admin approval), ordered by registration date ascending so
     * that the oldest pending applications appear first.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query filtering on {@code role = 'ngo'} AND {@code approved = 0}.
     * Used by the admin dashboard and manage-users page to surface pending
     * NGO applications that require action.
     *
     * @return a {@link List} of unapproved {@link User} objects with role
     *         {@code "ngo"}, ordered by {@code created_at} ascending;
     *         never {@code null}, empty if all NGOs are already approved
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Returns the total count of users registered with a specific role.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT COUNT(*)} aggregate query filtered by {@code role}.
     * Used by the admin dashboard and reports page to display platform-wide
     * statistics (e.g. total donors, total NGOs).
     *
     * @param role the role to count; one of {@code "donor"}, {@code "ngo"},
     *             or {@code "admin"}
     * @return the number of users with the specified role, or {@code 0} if
     *         the count query returns no rows
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Checks whether a given email address is already registered in the
     * {@code users} table.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT COUNT(*)} query filtered by {@code email}. Called by
     * {@link service.UserService#register} before inserting a new user to
     * prevent duplicate account registrations with the same email address.
     *
     * @param email the email address to check for existence
     * @return {@code true} if at least one user with this email exists in the
     *         database; {@code false} if the email is not yet registered
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Updates the editable profile fields of an existing user.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * statement on the {@code users} table, modifying only the {@code name},
     * {@code phone}, and {@code address} columns for the row identified by
     * {@code user.getId()}. The {@code email}, {@code role}, and
     * {@code password} columns are intentionally excluded from this update.
     *
     * @param user a {@link User} object whose {@code id}, {@code name},
     *             {@code phone}, and {@code address} fields contain the
     *             new values to persist; the email and role are not updated
     * @throws SQLException if a database access error occurs, or if no row
     *                      with the given id exists (zero rows affected)
     */

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

    /**
     * Replaces the stored BCrypt password hash for a specific user.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * statement on the {@code users} table, setting only the {@code password}
     * column for the row identified by {@code userId}. Called by
     * {@link service.UserService#changePassword} after the current password
     * has been verified and the new password has been hashed by
     * {@link util.PasswordUtil#hashPassword(String)}.
     *
     * @param userId         the primary key of the user whose password is
     *                       being changed
     * @param hashedPassword the new BCrypt hash to store; must not be a
     *                       plain-text password
     * @throws SQLException if a database access error occurs
     */

    public void updatePassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Sets the {@code approved} flag on a user account to either approve or
     * revoke approval for an NGO.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * statement setting {@code approved = ?} for the row identified by
     * {@code userId}. Called by admin-facing service methods:
     * {@link service.UserService#approveUser(int)} and
     * {@link service.UserService#rejectUser(int)}.
     *
     * <p>Approving an NGO ({@code approved = true}) allows it to log in.
     * Revoking approval ({@code approved = false}) blocks further logins
     * without deleting the account.
     *
     * @param userId   the primary key of the user to approve or reject
     * @param approved {@code true} to grant login access; {@code false} to
     *                 place the account back in a pending state
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Permanently deletes a user account and all associated data.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code DELETE}
     * statement on the {@code users} table for the row identified by
     * {@code userId}. Because the schema uses {@code ON DELETE CASCADE} on
     * the foreign keys of {@code food_items}, {@code requests}, and
     * {@code notifications}, all records belonging to this user are also
     * deleted automatically by the database engine.
     *
     * <p><strong>Note:</strong> Admin accounts should not be deleted through
     * the UI; the admin manage-users servlet enforces this restriction.
     *
     * @param userId the primary key of the user to delete
     * @throws SQLException if a database access error occurs
     */

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Maps a single row from a {@link ResultSet} to a {@link User} object.
     *
     * <p>This private helper is called by every read method to avoid
     * duplicating column-name literals and type-conversion logic across
     * multiple methods. The {@code created_at} column is converted from
     * a {@link Timestamp} to a {@link java.time.LocalDateTime} using
     * {@link Timestamp#toLocalDateTime()}.
     *
     * @param rs the {@link ResultSet} positioned on the row to map;
     *           the cursor must already be pointing at a valid row
     * @return a fully populated {@link User} object reflecting the current
     *         row of {@code rs}
     * @throws SQLException if any column value cannot be read from the
     *                      {@link ResultSet}
     */

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
