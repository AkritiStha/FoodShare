package dao;

import model.Notification;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for all CRUD operations on the
 * {@code notifications} database table in the FoodShare application.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the DAO layer and is called exclusively by
 * {@link service.NotificationService}. It provides in-app notification
 * persistence: notifications are written when significant events occur
 * (e.g. an NGO requests food, a donor accepts a request) and are read
 * when a user visits their dashboard. An unread-count query drives the
 * notification badge visible in the navigation bar.
 *
 * <p>All queries use {@link PreparedStatement} to prevent SQL injection,
 * and each method manages its own connection via try-with-resources.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.NotificationService
 * @see     model.Notification
 */

public class NotificationDAO {

    /**
     * Inserts a new notification record into the {@code notifications} table.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code INSERT}
     * with {@code is_read} defaulting to {@code 0} (unread) as defined by
     * the database schema. The notification is immediately visible to the
     * target user on their next dashboard visit.
     *
     * <p>This method is called by {@link service.RequestService} at each
     * significant lifecycle event:
     * <ul>
     *   <li>NGO submits a request  → notification sent to the donor.</li>
     *   <li>Donor accepts a request → notification sent to the NGO.</li>
     *   <li>Donor rejects a request → notification sent to the NGO.</li>
     *   <li>Donor completes pickup  → notification sent to the NGO.</li>
     * </ul>
     *
     * @param notif a {@link Notification} object with {@code userId} and
     *              {@code message} populated; {@code id} and {@code createdAt}
     *              are set by the database automatically
     * @throws SQLException if a database access error occurs, or if
     *                      {@code user_id} does not reference an existing user
     */

    public void createNotification(Notification notif) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notif.getUserId());
            ps.setString(2, notif.getMessage());
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves the most recent notifications for a specific user, limited
     * to the last 20 records.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query filtered by {@code user_id}, ordered by {@code created_at DESC}
     * so the newest notification appears first, with a {@code LIMIT 20}
     * clause to bound the result set size. Both read and unread notifications
     * are returned so the user sees their full recent history.
     *
     * <p>Called by dashboard servlets to populate the notification panel.
     * After display, {@link #markAllRead(int)} is called to clear the unread
     * state.
     *
     * @param userId the {@code users.id} of the user whose notifications
     *               are to be retrieved
     * @return a {@link List} of up to 20 {@link Notification} objects for
     *         the specified user, ordered newest first; never {@code null}
     * @throws SQLException if a database access error occurs
     */

    public List<Notification> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 20";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Returns the number of unread notifications for a specific user.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT COUNT(*)} query filtered on both {@code user_id} and
     * {@code is_read = 0}. The returned count is used by the navbar JSP
     * fragment to render the orange notification badge and by dashboard
     * servlets to pass the count as a request attribute.
     *
     * @param userId the {@code users.id} of the user to check
     * @return the number of unread notifications; {@code 0} if all
     *         notifications have been read or none exist
     * @throws SQLException if a database access error occurs
     */

    public int countUnread(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Marks all unread notifications for a specific user as read in a single
     * bulk update.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * setting {@code is_read = 1} for all rows where {@code user_id = ?}
     * AND {@code is_read = 0}. The {@code is_read = 0} guard prevents
     * unnecessary writes to already-read rows.
     *
     * <p>Called by dashboard servlets immediately after the notifications have
     * been fetched and set as a request attribute, ensuring that on the next
     * page load the badge counter resets to zero.
     *
     * @param userId the {@code users.id} of the user whose unread
     *               notifications should be marked as read
     * @throws SQLException if a database access error occurs
     */

    public void markAllRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read=1 WHERE user_id=? AND is_read=0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Maps a single {@link ResultSet} row to a {@link Notification} object.
     *
     * @param rs the {@link ResultSet} positioned on the row to map
     * @return a populated {@link Notification} object
     * @throws SQLException if any column cannot be read from the result set
     */

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
        return n;
    }
}
