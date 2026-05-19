package dao;

import model.FoodItem;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for all CRUD operations on the
 * {@code food_items} database table in the FoodShare application.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the DAO layer. It is the sole component that issues
 * SQL against the {@code food_items} table. Business logic — such as input
 * validation, distance sorting, and status transition rules — is handled in
 * {@link service.FoodService}, which delegates all persistence operations to
 * this class. This separation keeps the DAO focused exclusively on data
 * retrieval and mutation.
 *
 * <p><strong>Key design decisions:</strong>
 * <ul>
 *   <li>Read queries that display food to NGOs perform an implicit JOIN with
 *       the {@code users} table to retrieve the donor's name in a single
 *       round-trip, avoiding N+1 query problems.</li>
 *   <li>The {@link #markExpiredItems()} method is called defensively at the
 *       start of any read operation that returns food to NGOs, ensuring that
 *       expired listings are never surfaced even if the background job has not
 *       yet run.</li>
 *   <li>All write queries use {@link PreparedStatement} to prevent SQL
 *       injection.</li>
 * </ul>
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.FoodService
 * @see     util.DBConnection
 * @see     model.FoodItem
 */
public class FoodItemDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new food listing into the {@code food_items} table and
     * returns the generated primary key.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code INSERT}
     * statement. The {@code status} column is hard-coded to {@code 'available'}
     * at insert time; it transitions to other states (requested, completed,
     * expired) via {@link #updateStatus(int, String)} as the food item moves
     * through its lifecycle.
     *
     * @param item a populated {@link FoodItem} object. The {@code donorId},
     *             {@code name}, {@code quantity}, {@code quantityUnit},
     *             {@code description}, {@code expiryDate},
     *             {@code pickupLocation}, {@code latitude}, and
     *             {@code longitude} fields must be set. The {@code id} field
     *             is ignored and set from the generated key.
     * @return the auto-generated {@code food_items.id} of the new row, or
     *         {@code -1} if the insert succeeded but no key was returned
     * @throws SQLException if a database access error occurs, or if a
     *                      required foreign key ({@code donor_id}) does not
     *                      reference an existing user
     */

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

    /**
     * Retrieves a single food item by its primary key, including the donor's
     * display name via an inner JOIN.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT}
     * with an {@code INNER JOIN} on {@code users} to populate the transient
     * {@code donorName} field without requiring a separate query. Used by
     * {@link controller.UpdateFoodServlet} to pre-fill the edit form and by
     * the request flow to confirm food item ownership.
     *
     * @param id the primary key of the food item to retrieve
     * @return the matching {@link FoodItem} with {@code donorName} populated,
     *         or {@code null} if no item with the given {@code id} exists
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves all food items belonging to a specific donor, ordered by
     * creation date descending (most recently listed first).
     *
     * <p><strong>Database interaction:</strong> Executes a {@code SELECT}
     * with an {@code INNER JOIN} on {@code users}, filtered by
     * {@code donor_id}. Returns items of all statuses (available, requested,
     * completed, expired) so that the donor can see their full listing
     * history in the "My Listings" view.
     *
     * @param donorId the {@code users.id} of the donor whose listings
     *                are to be retrieved
     * @return a {@link List} of {@link FoodItem} objects owned by the
     *         specified donor, ordered by {@code created_at DESC};
     *         never {@code null}, may be empty
     * @throws SQLException if a database access error occurs
     */

    public List<FoodItem> findByDonor(int donorId) throws SQLException {
        String sql = "SELECT f.*, u.name AS donor_name " +
                "FROM food_items f JOIN users u ON f.donor_id = u.id " +
                "WHERE f.donor_id = ? ORDER BY f.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, donorId));
    }

    /**
     * Retrieves all food items currently available for NGO requests.
     *
     * <p><strong>Database interaction:</strong> Before running the main
     * {@code SELECT}, calls {@link #markExpiredItems()} to transition any
     * items whose {@code expiry_date} has passed to {@code status = 'expired'}.
     * The subsequent query filters on both {@code status = 'available'} and
     * {@code expiry_date > NOW()} as a double safety guard, and orders results
     * by {@code expiry_date ASC} so the items expiring soonest appear first —
     * encouraging NGOs to request time-sensitive food first.
     *
     * <p>The result list is then passed to
     * {@link util.DistanceCalculator#sortByDistance} by
     * {@link service.FoodService#searchAvailable} if the NGO has provided
     * location coordinates.
     *
     * @return a {@link List} of {@link FoodItem} objects with status
     *         {@code available} and a future expiry date, ordered by
     *         expiry date ascending; never {@code null}
     * @throws SQLException if a database access error occurs
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

    /**
     * Searches available food items whose {@code name} or {@code description}
     * column contains the given keyword (case-insensitive {@code LIKE} match).
     *
     * <p><strong>Database interaction:</strong> Calls {@link #markExpiredItems()}
     * first, then executes a {@code SELECT} filtered by
     * {@code status = 'available'}, {@code expiry_date > NOW()}, and a
     * disjunctive {@code LIKE} predicate on both {@code name} and
     * {@code description}. The keyword is wrapped with {@code %} wildcards so
     * a partial match on either side of the search term is accepted.
     *
     * @param keyword the search term to match against food item names and
     *                descriptions; must not be {@code null}
     * @return a {@link List} of matching available {@link FoodItem} objects,
     *         ordered by expiry date ascending; never {@code null}
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves every food item in the system, regardless of status, for use
     * by the admin "Manage Food" panel.
     *
     * <p><strong>Database interaction:</strong> Executes an unfiltered
     * {@code SELECT} with a JOIN on {@code users}, ordered by
     * {@code created_at DESC}. Admins see all statuses (available, requested,
     * completed, expired) and may delete any listing.
     *
     * @return a {@link List} of all {@link FoodItem} objects in the database,
     *         ordered by creation date descending; never {@code null}
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Returns the total number of food items across all statuses.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT COUNT(*)} aggregate query on the {@code food_items} table.
     * Used by the admin dashboard and reports page.
     *
     * @return the total row count of the {@code food_items} table
     * @throws SQLException if a database access error occurs
     */

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM food_items";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Calculates the total quantity (in the unit stored per item) of food
     * that has been successfully collected via completed requests.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT SUM(f.quantity)} with a JOIN on {@code requests}, filtered
     * to rows where {@code requests.status = 'COMPLETED'}. Returns {@code 0.0}
     * if no completed requests exist. Used as the "food waste reduced" metric
     * on the admin reports page.
     *
     * @return the sum of {@code quantity} values for all food items linked
     *         to completed requests; {@code 0.0} if there are none
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Updates all editable fields of an existing food item.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * statement on the {@code food_items} table, modifying the name, quantity,
     * unit, description, expiry date, pickup location, and coordinates.
     * The WHERE clause includes both {@code id} and {@code donor_id} to
     * prevent one donor from overwriting another donor's listing — a
     * row-level ownership check enforced at the database level.
     *
     * @param item a {@link FoodItem} object with updated field values;
     *             {@code id} and {@code donorId} are used in the WHERE clause
     *             and must match an existing owned row
     * @throws SQLException if a database access error occurs, or if no row
     *                      matching both {@code id} and {@code donor_id} exists
     */

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

    /**
     * Updates only the {@code status} column of a specific food item.
     *
     * <p><strong>Database interaction:</strong> Executes a targeted
     * {@code UPDATE SET status = ?} statement. This method drives the food
     * item lifecycle:
     * <ul>
     *   <li>{@code available → requested}: called by
     *       {@link service.RequestService#submitRequest} when an NGO requests
     *       the item.</li>
     *   <li>{@code requested → available}: called by
     *       {@link service.RequestService#rejectRequest} to reopen the item
     *       for other NGOs.</li>
     *   <li>{@code requested → completed}: called by
     *       {@link service.RequestService#completeRequest} after successful
     *       pickup.</li>
     *   <li>{@code available → expired}: called by
     *       {@link #markExpiredItems()} via a bulk SQL update.</li>
     * </ul>
     *
     * @param foodItemId the primary key of the food item to update
     * @param status     the new status string; must be one of
     *                   {@code "available"}, {@code "requested"},
     *                   {@code "completed"}, or {@code "expired"}
     * @throws SQLException if a database access error occurs
     */

    public void updateStatus(int foodItemId, String status) throws SQLException {
        String sql = "UPDATE food_items SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, foodItemId);
            ps.executeUpdate();
        }
    }

    /**
     * Performs a bulk status update, transitioning all food items whose
     * {@code expiry_date} has passed (and are still {@code 'available'}) to
     * {@code 'expired'}.
     *
     * <p><strong>Database interaction:</strong> Executes a single
     * {@code UPDATE} statement affecting potentially many rows:
     * {@code WHERE expiry_date <= NOW() AND status = 'available'}.
     * The {@code status = 'available'} guard prevents overwriting items
     * already in a {@code requested} or {@code completed} state.
     *
     * <p>This method is called defensively at the start of
     * {@link #findAvailable()} and {@link #searchAvailable(String)}, and
     * explicitly by {@link controller.MyListingsServlet} before showing the
     * donor their listings, so that the displayed statuses always reflect
     * real-world expiry without requiring a scheduled background job.
     *
     * @throws SQLException if a database access error occurs
     */

    public void markExpiredItems() throws SQLException {
        String sql = "UPDATE food_items SET status='expired' " +
                "WHERE expiry_date <= NOW() AND status = 'available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Deletes a food item owned by a specific donor.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code DELETE}
     * statement with a WHERE clause on both {@code id} and {@code donor_id}.
     * The ownership check prevents a donor from deleting another donor's
     * listing. Associated request and notification records are removed by the
     * database's {@code ON DELETE CASCADE} constraint.
     *
     * @param id      the primary key of the food item to delete
     * @param donorId the {@code users.id} of the donor who must own the item;
     *                the delete silently has no effect if the item belongs to
     *                a different donor
     * @throws SQLException if a database access error occurs
     */

    public void deleteFoodItem(int id, int donorId) throws SQLException {
        String sql = "DELETE FROM food_items WHERE id=? AND donor_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, donorId);
            ps.executeUpdate();
        }
    }

    /**
     * Deletes any food item by primary key, bypassing ownership checks.
     * Intended exclusively for use by the admin "Manage Food" panel.
     *
     * <p><strong>Database interaction:</strong> Executes a {@code DELETE}
     * statement filtered only by {@code id}. This method should never be
     * called from donor-facing code; the ownership-checking
     * {@link #deleteFoodItem(int, int)} must be used in all donor contexts.
     *
     * @param id the primary key of the food item to delete
     * @throws SQLException if a database access error occurs
     */

    public void adminDeleteFoodItem(int id) throws SQLException {
        String sql = "DELETE FROM food_items WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Functional interface used as a parameter type for the
     * {@link #queryList(String, PrepareStatement)} helper, allowing
     * callers to supply a lambda that sets {@link PreparedStatement}
     * parameters without boilerplate try-catch wrappers.
     */

    @FunctionalInterface
    interface PrepareStatement { void prepare(PreparedStatement ps) throws SQLException; }

    /**
     * Generic helper that executes a parameterised SELECT query and returns
     * the results as a list of {@link FoodItem} objects.
     *
     * <p>Reduces boilerplate by centralising connection acquisition,
     * statement preparation, and result-set iteration. Each caller supplies
     * a lambda to set the query parameters.
     *
     * @param sql the parameterised SQL {@code SELECT} statement to execute
     * @param p   a lambda that sets the required {@link PreparedStatement}
     *            parameters for {@code sql}
     * @return a list of {@link FoodItem} objects mapped from the result set;
     *         never {@code null}, may be empty
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Maps a single {@link ResultSet} row to a fully populated
     * {@link FoodItem} object.
     *
     * <p>Handles the {@code Timestamp}-to-{@code LocalDateTime} conversion
     * for {@code expiry_date}, {@code created_at}, and {@code updated_at},
     * guarding against {@code null} timestamps (e.g. before the row is
     * committed). The transient {@code donorName} field is populated from
     * the JOIN alias {@code donor_name}.
     *
     * @param rs the {@link ResultSet} positioned on the row to map
     * @return a populated {@link FoodItem}
     * @throws SQLException if any column value cannot be read
     */

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
