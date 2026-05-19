package dao;

import model.PickupSchedule;
import util.DBConnection;

import java.sql.*;

/**
 * Data Access Object (DAO) responsible for persisting and retrieving
 * pickup schedule records from the {@code pickup_schedules} database table.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the DAO layer and is called by
 * {@link service.RequestService#acceptRequest} when a donor accepts an NGO
 * request and specifies a pickup date and time. The {@code pickup_schedules}
 * table has a one-to-one relationship with the {@code requests} table
 * (enforced by a UNIQUE constraint on {@code request_id}): every accepted
 * request may have at most one pickup schedule.
 *
 * <p>All queries use {@link PreparedStatement} to prevent SQL injection.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.RequestService
 * @see     model.PickupSchedule
 */

public class PickupScheduleDAO {

    /**
     * Inserts a new pickup schedule, or updates an existing one if a
     * schedule for the same request already exists.
     *
     * <p><strong>Database interaction:</strong> Executes an
     * {@code INSERT ... ON DUPLICATE KEY UPDATE} statement. Because
     * {@code request_id} is defined as {@code UNIQUE} in the schema, a
     * second call with the same {@code requestId} silently updates the
     * {@code pickup_time} and {@code notes} columns rather than throwing a
     * duplicate-key error. This allows a donor to revise a scheduled pickup
     * time without needing to delete the existing record first.
     *
     * <p>Called by {@link service.RequestService#acceptRequest} immediately
     * after the request status has been set to {@code ACCEPTED}.
     *
     * @param schedule a {@link PickupSchedule} object with {@code requestId},
     *                 {@code pickupTime}, and optionally {@code notes} set.
     *                 The {@code id} field is ignored on insert and is not
     *                 returned by this method.
     * @throws SQLException if a database access error occurs, or if the
     *                      {@code request_id} does not reference an existing
     *                      request record
     */

    public void createOrUpdate(PickupSchedule schedule) throws SQLException {
        String sql = "INSERT INTO pickup_schedules (request_id, pickup_time, notes) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE pickup_time=VALUES(pickup_time), notes=VALUES(notes)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getRequestId());
            ps.setTimestamp(2, Timestamp.valueOf(schedule.getPickupTime()));
            ps.setString(3, schedule.getNotes());
            ps.executeUpdate();
        }
    }

    /**
     * Retrieves the pickup schedule associated with a specific request.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT *}
     * query filtered by {@code request_id}. Returns {@code null} if no
     * schedule exists yet (i.e. the request is still PENDING).
     *
     * <p>Note: in practice, the {@link dao.RequestDAO} read queries already
     * retrieve {@code pickup_time} via a LEFT JOIN on {@code pickup_schedules},
     * so this method is available for cases where only the schedule record
     * itself is needed (e.g. fetching the donor's optional notes separately).
     *
     * @param requestId the {@code requests.id} whose pickup schedule is
     *                  to be retrieved
     * @return the matching {@link PickupSchedule} object, or {@code null}
     *         if no schedule has been set for this request
     * @throws SQLException if a database access error occurs
     */

    public PickupSchedule findByRequestId(int requestId) throws SQLException {
        String sql = "SELECT * FROM pickup_schedules WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PickupSchedule s = new PickupSchedule();
                    s.setId(rs.getInt("id"));
                    s.setRequestId(rs.getInt("request_id"));
                    Timestamp pt = rs.getTimestamp("pickup_time");
                    if (pt != null) s.setPickupTime(pt.toLocalDateTime());
                    s.setNotes(rs.getString("notes"));
                    Timestamp cr = rs.getTimestamp("created_at");
                    if (cr != null) s.setCreatedAt(cr.toLocalDateTime());
                    return s;
                }
            }
        }
        return null;
    }
}
