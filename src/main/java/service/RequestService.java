package service;

import dao.FoodItemDAO;
import dao.NotificationDAO;
import dao.PickupScheduleDAO;
import dao.RequestDAO;
import model.FoodItem;
import model.Notification;
import model.PickupSchedule;
import model.Request;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for request lifecycle management.
 * Handles the flow: PENDING → ACCEPTED → COMPLETED (or REJECTED).
 */
public class RequestService {

    private final RequestDAO       requestDAO       = new RequestDAO();
    private final FoodItemDAO      foodItemDAO      = new FoodItemDAO();
    private final NotificationDAO  notificationDAO  = new NotificationDAO();
    private final PickupScheduleDAO scheduleDAO     = new PickupScheduleDAO();

    /**
     * NGO submits a request for an available food item.
     *
     * @return null on success, or an error message string
     */
    public String submitRequest(int foodItemId, int ngoId, String message) {
        try {
            FoodItem item = foodItemDAO.findById(foodItemId);
            if (item == null)                          return "Food item not found.";
            if (!"available".equals(item.getStatus())) return "This food item is no longer available.";
            if (item.isExpired())                      return "This food item has expired.";

            // Prevent duplicate requests from same NGO
            if (requestDAO.existsRequest(foodItemId, ngoId))
                return "You have already requested this item.";

            // Create the request
            Request req = new Request();
            req.setFoodItemId(foodItemId);
            req.setNgoId(ngoId);
            req.setDonorId(item.getDonorId());
            req.setMessage(message);
            int requestId = requestDAO.createRequest(req);
            if (requestId < 0) return "Failed to submit request.";

            // Mark food item as 'requested'
            foodItemDAO.updateStatus(foodItemId, "requested");

            // Notify the donor
            Notification n = new Notification(
                    item.getDonorId(),
                    "An NGO has requested your food listing: \"" + item.getName() + "\"."
            );
            notificationDAO.createNotification(n);

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    /**
     * Donor accepts an NGO request and sets a pickup schedule.
     *
     * @return null on success, or an error message string
     */
    public String acceptRequest(int requestId, int donorId,
                                String pickupTimeStr, String scheduleNotes) {
        if (ValidationUtil.isBlank(pickupTimeStr)) return "Please provide a pickup time.";

        try {
            Request req = requestDAO.findById(requestId);
            if (req == null || req.getDonorId() != donorId)
                return "Request not found or access denied.";
            if (!"PENDING".equals(req.getStatus()))
                return "This request is no longer pending.";

            // Update request status
            requestDAO.updateStatus(requestId, "ACCEPTED");

            // Save pickup schedule
            PickupSchedule schedule = new PickupSchedule();
            schedule.setRequestId(requestId);
            schedule.setPickupTime(ValidationUtil.parseDateTime(pickupTimeStr));
            schedule.setNotes(scheduleNotes);
            scheduleDAO.createOrUpdate(schedule);

            // Notify the NGO
            Notification n = new Notification(
                    req.getNgoId(),
                    "Your request for \"" + req.getFoodItemName() + "\" has been ACCEPTED. " +
                            "Pickup scheduled."
            );
            notificationDAO.createNotification(n);

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    /**
     * Donor rejects an NGO request.
     * Reverts the food item to 'available' so others can request it.
     */
    public String rejectRequest(int requestId, int donorId) {
        try {
            Request req = requestDAO.findById(requestId);
            if (req == null || req.getDonorId() != donorId)
                return "Request not found or access denied.";

            requestDAO.updateStatus(requestId, "REJECTED");

            // Revert food item to available
            foodItemDAO.updateStatus(req.getFoodItemId(), "available");

            // Notify the NGO
            Notification n = new Notification(
                    req.getNgoId(),
                    "Your request for \"" + req.getFoodItemName() + "\" has been declined by the donor."
            );
            notificationDAO.createNotification(n);

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    /**
     * Donor marks a request as completed after pickup.
     */
    public String completeRequest(int requestId, int donorId) {
        try {
            Request req = requestDAO.findById(requestId);
            if (req == null || req.getDonorId() != donorId)
                return "Request not found or access denied.";
            if (!"ACCEPTED".equals(req.getStatus()))
                return "Only accepted requests can be completed.";

            requestDAO.updateStatus(requestId, "COMPLETED");
            foodItemDAO.updateStatus(req.getFoodItemId(), "completed");

            // Notify the NGO
            Notification n = new Notification(
                    req.getNgoId(),
                    "The donor has marked your pickup of \"" + req.getFoodItemName() + "\" as COMPLETED. Thank you!"
            );
            notificationDAO.createNotification(n);

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    /**
     * NGO rates a completed donation (1–5 stars).
     */
    public String submitRating(int requestId, int ngoId, int rating, String note) {
        if (rating < 1 || rating > 5) return "Rating must be between 1 and 5.";
        try {
            Request req = requestDAO.findById(requestId);
            if (req == null || req.getNgoId() != ngoId)
                return "Request not found or access denied.";
            if (!"COMPLETED".equals(req.getStatus()))
                return "You can only rate completed pickups.";
            if (req.getRating() != null)
                return "You have already rated this donation.";

            requestDAO.saveRating(requestId, rating, note);
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return "A database error occurred.";
        }
    }

    // ── Query helpers ─────────────────────────────────────────────────────────

    public Request getById(int id) throws SQLException {
        return requestDAO.findById(id);
    }

    public List<Request> getDonorRequests(int donorId) throws SQLException {
        return requestDAO.findByDonor(donorId);
    }

    public List<Request> getNgoRequests(int ngoId) throws SQLException {
        return requestDAO.findByNgo(ngoId);
    }

    public List<Request> getAllRequests() throws SQLException {
        return requestDAO.findAll();
    }

    public int countAll() throws SQLException {
        return requestDAO.countAll();
    }

    public int countByStatus(String status) throws SQLException {
        return requestDAO.countByStatus(status);
    }

    public String topDonorName() throws SQLException {
        return requestDAO.topDonorName();
    }

    public String mostRequestedFood() throws SQLException {
        return requestDAO.mostRequestedFood();
    }
}
