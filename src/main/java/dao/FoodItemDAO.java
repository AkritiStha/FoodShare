package dao;

import model.FoodItem;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the food_items table.
 */
public class FoodItemDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    public int createFoodItem(FoodItem item) throws SQLException {
        String sql = "INSERT INTO food_items " +
                "(donor_id, name, quantity, quantity_unit, description, " +
                " expiry_date, pickup_location, latitude, longitude, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'available')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, item.getDonorId());
            ps.setString(2, item.getName());
            ps.setBigDecimal(3, item.getQuantity());
            ps.setString(4, item.getQuantityUnit());
            ps.setString(5, item.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(item.getExpiryDate()));
            ps.setString(7, item.getPickupLocation());
            ps.setDouble(8, item.getLatitude());
            ps.setDouble(9, item.getLongitude());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public FoodItem findById(int id) throws SQLException {
        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "WHERE f.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** All food items for a specific donor. */
    public List<FoodItem> findByDonor(int donorId) throws SQLException {
        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "WHERE f.donor_id = ? ORDER BY f.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, donorId));
    }

    /**
     * Available (non-expired) food for NGO search.
     * Expired items are auto-updated to 'expired' status before querying.
     */
    public List<FoodItem> findAvailable() throws SQLException {
        // First, mark any past-expiry items as expired
        markExpiredItems();

        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "WHERE f.status = 'available' " +
                "  AND f.expiry_date > NOW() " +
                "ORDER BY f.expiry_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<FoodItem> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    /** Available food filtered by name keyword. */
    public List<FoodItem> searchAvailable(String keyword) throws SQLException {
        markExpiredItems();
        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "WHERE f.status = 'available' " +
                "  AND f.expiry_date > NOW() " +
                "  AND (f.name LIKE ? OR f.description LIKE ?) " +
                "ORDER BY f.expiry_date ASC";

        String pattern = "%" + keyword + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            List<FoodItem> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        }
    }

    /** All food items for admin view. */
    public List<FoodItem> findAll() throws SQLException {
        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "ORDER BY f.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<FoodItem> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM food_items";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /** Sum of quantities for completed requests (food waste reduced metric). */
    public double totalFoodSaved() throws SQLException {
        String sql = "SELECT COALESCE(SUM(f.quantity), 0) " +
                "FROM food_items f JOIN requests r ON f.id = r.food_item_id " +
                "WHERE r.status = 'COMPLETED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public void updateFoodItem(FoodItem item) throws SQLException {
        String sql = "UPDATE food_items SET name=?, quantity=?, quantity_unit=?, description=?, " +
                "expiry_date=?, pickup_location=?, latitude=?, longitude=? WHERE id=? AND donor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setBigDecimal(2, item.getQuantity());
            ps.setString(3, item.getQuantityUnit());
            ps.setString(4, item.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(item.getExpiryDate()));
            ps.setString(6, item.getPickupLocation());
            ps.setDouble(7, item.getLatitude());
            ps.setDouble(8, item.getLongitude());
            ps.setInt(9, item.getId());
            ps.setInt(10, item.getDonorId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int foodItemId, String status) throws SQLException {
        String sql = "UPDATE food_items SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, foodItemId);
            ps.executeUpdate();
        }
    }

    /** Auto-marks all items past their expiry date as 'expired'. */
    public void markExpiredItems() throws SQLException {
        String sql = "UPDATE food_items SET status='expired' " +
                "WHERE expiry_date <= NOW() AND status = 'available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteFoodItem(int id, int donorId) throws SQLException {
        String sql = "DELETE FROM food_items WHERE id=? AND donor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, donorId);
            ps.executeUpdate();
        }
    }

    /** Admin can delete any food item. */
    public void adminDeleteFoodItem(int id) throws SQLException {
        String sql = "DELETE FROM food_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface PrepareStatement { void prepare(PreparedStatement ps) throws SQLException; }

    private List<FoodItem> queryList(String sql, PrepareStatement p) throws SQLException {
        List<FoodItem> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            p.prepare(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private FoodItem mapRow(ResultSet rs) throws SQLException {
        FoodItem f = new FoodItem();
        f.setId(rs.getInt("id"));
        f.setDonorId(rs.getInt("donor_id"));
        f.setDonorName(rs.getString("donor_name"));
        f.setName(rs.getString("name"));
        f.setQuantity(rs.getBigDecimal("quantity"));
        f.setQuantityUnit(rs.getString("quantity_unit"));
        f.setDescription(rs.getString("description"));
        Timestamp exp = rs.getTimestamp("expiry_date");
        if (exp != null) f.setExpiryDate(exp.toLocalDateTime());
        f.setPickupLocation(rs.getString("pickup_location"));
        f.setLatitude(rs.getDouble("latitude"));
        f.setLongitude(rs.getDouble("longitude"));
        f.setStatus(rs.getString("status"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) f.setCreatedAt(cr.toLocalDateTime());
        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null) f.setUpdatedAt(up.toLocalDateTime());
        return f;
    }
}
