package dao;

import model.Request;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the requests table.
 */
public class RequestDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    public int createRequest(Request req) throws SQLException {
        String sql = "INSERT INTO requests (food_item_id, ngo_id, donor_id, status, message) " +
                "VALUES (?, ?, ?, 'PENDING', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, req.getFoodItemId());
            ps.setInt(2, req.getNgoId());
            ps.setInt(3, req.getDonorId());
            ps.setString(4, req.getMessage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public Request findById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Requests received by a donor (they need to accept/reject). */
    public List<Request> findByDonor(int donorId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.donor_id = ? ORDER BY r.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, donorId));
    }

    /** Requests made by an NGO. */
    public List<Request> findByNgo(int ngoId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.ngo_id = ? ORDER BY r.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, ngoId));
    }

    /** All requests for admin view. */
    public List<Request> findAll() throws SQLException {
        String sql = BASE_SELECT + " ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Request> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Checks whether an NGO has already requested a specific food item. */
    public boolean existsRequest(int foodItemId, int ngoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests " +
                "WHERE food_item_id = ? AND ngo_id = ? AND status NOT IN ('REJECTED','EXPIRED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodItemId);
            ps.setInt(2, ngoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void updateStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE requests SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    /** Saves NGO rating for a completed pickup. */
    public void saveRating(int requestId, int rating, String note) throws SQLException {
        String sql = "UPDATE requests SET rating=?, rating_note=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, note);
            ps.setInt(3, requestId);
            ps.executeUpdate();
        }
    }

    // ── Admin report queries ──────────────────────────────────────────────────

    /** Returns donor name with most completed donations. */
    public String topDonorName() throws SQLException {
        String sql = "SELECT u.name, COUNT(*) AS cnt " +
                "FROM requests r JOIN users u ON r.donor_id = u.id " +
                "WHERE r.status = 'COMPLETED' " +
                "GROUP BY r.donor_id ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("name");
        }
        return "N/A";
    }

    /** Returns the most requested food item name. */
    public String mostRequestedFood() throws SQLException {
        String sql = "SELECT f.name, COUNT(*) AS cnt " +
                "FROM requests r JOIN food_items f ON r.food_item_id = f.id " +
                "GROUP BY r.food_item_id ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("name");
        }
        return "N/A";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static final String BASE_SELECT =
            "SELECT r.*, " +
                    "       f.name AS food_item_name, " +
                    "       f.pickup_location, " +
                    "       n.name AS ngo_name, " +
                    "       d.name AS donor_name, " +
                    "       ps.pickup_time " +
                    "FROM requests r " +
                    "JOIN food_items f ON r.food_item_id = f.id " +
                    "JOIN users n ON r.ngo_id = n.id " +
                    "JOIN users d ON r.donor_id = d.id " +
                    "LEFT JOIN pickup_schedules ps ON ps.request_id = r.id ";

    @FunctionalInterface
    interface PrepareStatement { void prepare(PreparedStatement ps) throws SQLException; }

    private List<Request> queryList(String sql, PrepareStatement p) throws SQLException {
        List<Request> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            p.prepare(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Request mapRow(ResultSet rs) throws SQLException {
        Request r = new Request();
        r.setId(rs.getInt("id"));
        r.setFoodItemId(rs.getInt("food_item_id"));
        r.setNgoId(rs.getInt("ngo_id"));
        r.setDonorId(rs.getInt("donor_id"));
        r.setStatus(rs.getString("status"));
        r.setMessage(rs.getString("message"));
        int rating = rs.getInt("rating");
        if (!rs.wasNull()) r.setRating(rating);
        r.setRatingNote(rs.getString("rating_note"));
        r.setFoodItemName(rs.getString("food_item_name"));
        r.setNgoName(rs.getString("ngo_name"));
        r.setDonorName(rs.getString("donor_name"));
        r.setPickupLocation(rs.getString("pickup_location"));
        Timestamp pt = rs.getTimestamp("pickup_time");
        if (pt != null) r.setPickupTime(pt.toLocalDateTime());
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) r.setCreatedAt(cr.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) r.setUpdatedAt(up.toLocalDateTime());
        return r;
    }
}
