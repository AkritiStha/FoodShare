package dao;

import model.Request;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for all CRUD operations on the
 * {@code requests} database table in the FoodShare application.
 *
 * <p><strong>Role in MVC Architecture:</strong><br>
 * This class belongs to the DAO layer and is the sole component that issues
 * SQL against the {@code requests} table. It is called exclusively by
 * {@link service.RequestService}, which owns all business rules around the
 * request lifecycle (PENDING → ACCEPTED → COMPLETED / REJECTED).
 *
 * <p><strong>Query design:</strong><br>
 * All read queries perform a four-way JOIN:
 * {@code requests ⟶ food_items ⟶ users (donor)} and
 * {@code requests ⟶ users (NGO)}, plus an optional LEFT JOIN on
 * {@code pickup_schedules}. This means a single SQL round-trip populates
 * all display-relevant fields on the returned {@link Request} objects,
 * including food name, donor name, NGO name, pickup location, and scheduled
 * pickup time.
 *
 * <p><strong>SQL-injection prevention:</strong><br>
 * All write operations use {@link PreparedStatement} with parameterised
 * placeholders.
 *
 * @author  FoodShare Team
 * @version 1.0
 * @see     service.RequestService
 * @see     model.Request
 */

public class RequestDAO {

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Inserts a new food request record into the {@code requests} table.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code INSERT}
     * statement. The {@code status} column is hard-coded to {@code 'PENDING'}
     * at creation time. Both {@code food_item_id} and {@code donor_id} are
     * required foreign keys; the donor's id is derived from the food item's
     * owner and is stored here for efficient donor-specific filtering without
     * additional JOINs.
     *
     * @param req a {@link Request} object with {@code foodItemId}, {@code ngoId},
     *            {@code donorId}, and optionally {@code message} set;
     *            {@code id} is ignored and populated from the generated key
     * @return the auto-generated {@code requests.id} of the new row, or
     *         {@code -1} if the insert succeeded but no generated key was
     *         returned
     * @throws SQLException if a database access error occurs, or if a
     *                      required foreign key constraint is violated
     */

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

    /**
     * Retrieves a single request by its primary key, with all JOIN fields
     * populated.
     *
     * <p><strong>Database interaction:</strong> Appends {@code WHERE r.id = ?}
     * to {@link #BASE_SELECT}. Used by {@link service.RequestService} in the
     * accept, reject, and complete operations to load the request and verify
     * ownership before applying a status change.
     *
     * @param id the primary key of the request to retrieve
     * @return the matching {@link Request} object with all joined display
     *         fields populated, or {@code null} if no request with the given
     *         {@code id} exists
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Retrieves all requests directed at a specific donor, ordered by most
     * recent first.
     *
     * <p><strong>Database interaction:</strong> Filters on
     * {@code r.donor_id = ?}. Used by the donor dashboard and the
     * "Requests Received" page to show incoming NGO requests that the
     * donor needs to accept, reject, or complete.
     *
     * @param donorId the {@code users.id} of the donor
     * @return a {@link List} of {@link Request} objects addressed to this
     *         donor, ordered by {@code created_at DESC}; never {@code null}
     * @throws SQLException if a database access error occurs
     */

    public List<Request> findByDonor(int donorId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.donor_id = ? ORDER BY r.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, donorId));
    }

    /**
     * Retrieves all requests submitted by a specific NGO, ordered by most
     * recent first.
     *
     * <p><strong>Database interaction:</strong> Filters on
     * {@code r.ngo_id = ?}. Used by the NGO dashboard and "My Requests"
     * page to show the NGO's request history and current statuses.
     *
     * @param ngoId the {@code users.id} of the NGO
     * @return a {@link List} of {@link Request} objects submitted by this
     *         NGO, ordered by {@code created_at DESC}; never {@code null}
     * @throws SQLException if a database access error occurs
     */

    public List<Request> findByNgo(int ngoId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.ngo_id = ? ORDER BY r.created_at DESC";
        return queryList(sql, ps -> ps.setInt(1, ngoId));
    }

    /**
     * Retrieves all requests in the system, ordered by most recent first.
     * Used exclusively by the admin reports and monitoring pages.
     *
     * @return a {@link List} of all {@link Request} objects, ordered by
     *         {@code created_at DESC}; never {@code null}
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Returns the total number of request records across all statuses.
     *
     * @return the row count of the {@code requests} table
     * @throws SQLException if a database access error occurs
     */

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM requests";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Returns the number of requests with a specific status value.
     * Used by the admin dashboard to display COMPLETED, PENDING, and
     * REJECTED counts as separate KPIs.
     *
     * @param status the status to count; one of {@code "PENDING"},
     *               {@code "ACCEPTED"}, {@code "COMPLETED"},
     *               {@code "REJECTED"}, or {@code "EXPIRED"}
     * @return the count of requests with the given status
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Checks whether an NGO has an active (non-rejected, non-expired) request
     * for a specific food item.
     *
     * <p><strong>Database interaction:</strong> Executes a
     * {@code SELECT COUNT(*)} filtered on both {@code food_item_id} and
     * {@code ngo_id}, excluding rows with status {@code REJECTED} or
     * {@code EXPIRED}. Called by {@link service.RequestService#submitRequest}
     * to prevent an NGO from submitting duplicate requests for the same item.
     *
     * @param foodItemId the primary key of the food item to check
     * @param ngoId      the primary key of the NGO making the request
     * @return {@code true} if an active request from this NGO for this food
     *         item already exists; {@code false} otherwise
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Updates the {@code status} column of a specific request record.
     *
     * <p>This is the core state-transition method for the request lifecycle.
     * It is always called by {@link service.RequestService} after all business
     * rule checks (ownership, valid transition) have passed.
     *
     * @param requestId the primary key of the request to update
     * @param status    the new status string; must be one of {@code "PENDING"},
     *                  {@code "ACCEPTED"}, {@code "COMPLETED"},
     *                  {@code "REJECTED"}, or {@code "EXPIRED"}
     * @throws SQLException if a database access error occurs
     */

    public void updateStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE requests SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    /**
     * Persists an NGO's star rating and optional comment for a completed
     * request.
     *
     * <p><strong>Database interaction:</strong> Executes an {@code UPDATE}
     * setting the {@code rating} (integer 1–5) and {@code rating_note}
     * columns for the specified request. Called by
     * {@link service.RequestService#submitRating} after verifying the request
     * is COMPLETED and has not already been rated.
     *
     * @param requestId the primary key of the request being rated
     * @param rating    the star rating, between 1 and 5 inclusive
     * @param note      an optional free-text comment from the NGO;
     *                  may be {@code null} or empty
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Returns the display name of the donor with the highest number of
     * completed donations.
     *
     * <p><strong>Database interaction:</strong> Executes a GROUP BY aggregate
     * query joining {@code requests} with {@code users}, filtering on
     * {@code status = 'COMPLETED'}, grouped by {@code donor_id}, ordered by
     * the count descending, and limited to 1 row. Used on the admin
     * dashboard and reports page as the "Top Donor" highlight.
     *
     * @return the {@code name} of the top donor, or {@code "N/A"} if no
     *         completed requests exist
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Returns the name of the food item that has been requested most
     * frequently across all statuses.
     *
     * <p><strong>Database interaction:</strong> Executes a GROUP BY aggregate
     * query joining {@code requests} with {@code food_items}, grouped by
     * {@code food_item_id}, ordered by request count descending, limited to
     * 1 row. Used on the admin reports page as the "Most Requested Food"
     * highlight.
     *
     * @return the {@code name} of the most frequently requested food item,
     *         or {@code "N/A"} if no requests have been made
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Base SELECT clause shared by all read methods.
     * Performs JOINs with {@code food_items}, donor {@code users},
     * NGO {@code users}, and an optional LEFT JOIN with
     * {@code pickup_schedules} to populate all display fields in one query.
     */

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

    // ── Private helpers ───────────────────────────────────────────────────────

    /** @see FoodItemDAO.PrepareStatement */

    @FunctionalInterface
    interface PrepareStatement { void prepare(PreparedStatement ps) throws SQLException; }

    /**
     * Executes a parameterised SELECT query and returns a list of
     * {@link Request} objects mapped from the result set.
     *
     * @param sql the parameterised SQL statement to execute
     * @param p   a lambda that sets {@link PreparedStatement} parameters
     * @return a list of mapped {@link Request} objects; never {@code null}
     * @throws SQLException if a database access error occurs
     */

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

    /**
     * Maps a single {@link ResultSet} row (including JOIN columns) to a
     * fully populated {@link Request} object.
     *
     * <p>Handles nullable columns safely: {@code rating} uses
     * {@link ResultSet#wasNull()} to distinguish a stored zero from a
     * {@code NULL}; {@code pickup_time} is only set when the LEFT JOIN
     * on {@code pickup_schedules} produces a non-null result.
     *
     * @param rs the {@link ResultSet} positioned on the row to map
     * @return a populated {@link Request} object
     * @throws SQLException if any column cannot be read
     */

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
