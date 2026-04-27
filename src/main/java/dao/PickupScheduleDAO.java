package dao;

import model.PickupSchedule;
import util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for pickup_schedules table.
 */
public class PickupScheduleDAO {

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
